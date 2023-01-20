package skunk.migrate

import cats.*
import cats.data.Kleisli
import cats.effect.*
import cats.syntax.all.*
import skunk.*
import skunk.codec.all.*
import skunk.data.*
import skunk.syntax.all.*

class Migrator[F[_]: MonadCancelThrow](
  val db: Resource[F, Session[F]],
  val migrations: List[Migration[F]],
):
  protected type SessionKleisli[A] = Kleisli[F, Session[F], A]

  protected def tableName: String = "schema_migrations"

  protected def transaction[A](usage: SessionKleisli[A]): SessionKleisli[A] = Kleisli { session =>
    session.transaction.surround {
      usage(session)
    }
  }

  protected def init: SessionKleisli[Unit] = Kleisli { session =>
    session
      .execute(
        sql"""
          CREATE TABLE IF NOT EXISTS #$tableName (
            uniq boolean
              GENERATED ALWAYS AS (TRUE) STORED
              UNIQUE,
            version bigint
              NOT NULL
          )
        """.command
      )
      .void
  }

  protected def lock: SessionKleisli[Unit] = Kleisli { session =>
    session
      .execute(sql"LOCK TABLE ONLY #$tableName IN ACCESS EXCLUSIVE MODE NOWAIT".command)
      .void
  }

  protected def getVersion: SessionKleisli[Option[Long]] = Kleisli { session =>
    session.option(sql"SELECT version FROM #$tableName".query(int8))
  }

  protected def setVersion(version: Option[Long]): SessionKleisli[Unit] = Kleisli { session =>
    version match
      case Some(v) =>
        session
          .prepare(
            sql"""
              INSERT INTO #$tableName (version) VALUES ($int8)
              ON CONFLICT (uniq) DO UPDATE SET version = EXCLUDED.version
            """.command
          )
          .flatMap(_.execute(v).void)

      case None =>
        session
          .execute(sql"DELETE FROM #$tableName".command)
          .void
  }

  protected def run(selectMigrations: Option[Long] => List[SessionKleisli[Option[Long]]]): F[Unit] =
    db.useKleisli {
      for
        _ <- init
        _ <- transaction {
          for
            _ <- lock
            v <- getVersion
            _ <- selectMigrations(v).traverse_ { m =>
              m >>= setVersion
            }
          yield ()
        }
      yield ()
    }

  def version: F[Option[Long]] =
    db.useKleisli(init *> getVersion)

  def upAll: F[Unit] =
    run { version =>
      val migrationSet = MigrationSet.atVersion(migrations, version)

      migrationSet.unapplied.map { m =>
        Kleisli(m.up).as(m.version.some)
      }
    }

  def downAll: F[Unit] =
    run { version =>
      val migrationSet = MigrationSet.atVersion(migrations, version)

      val versions =
        migrationSet.appliedReversible.drop(1).map(_.version.some)
          :+ migrationSet.appliedIrreversible.headOption.map(_.version)

      migrationSet.appliedReversible.zip(versions).map { (m, v) =>
        Kleisli(m.down).as(v)
      }
    }

object Migrator:
  inline def discoverMigrations[F[_]](inline packageName: String): List[Migration[F]] =
    ${ Macros.discoverMigrations[F]('packageName) }

  def apply[F[_]: MonadCancelThrow](
    db: Resource[F, Session[F]],
    migrations: List[Migration[F]],
  ): Migrator[F] =
    new Migrator(db, migrations)

  inline def apply[F[_]: MonadCancelThrow](
    db: Resource[F, Session[F]],
    inline migrationsPackage: String,
  ): Migrator[F] =
    Migrator(db, discoverMigrations(migrationsPackage))
