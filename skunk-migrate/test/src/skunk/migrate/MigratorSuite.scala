package skunk.migrate

import cats.effect.IO
import cats.effect.Resource
import cats.effect.std.Random
import cats.syntax.all.*
import munit.CatsEffectSuite
import natchez.Trace
import skunk.Session
import skunk.codec.numeric.int8
import skunk.exception.PostgresErrorException
import skunk.migrate.testmigrations.*
import skunk.syntax.all.*

class MigratorSuite extends CatsEffectSuite:
  given Trace[IO] = Trace.Implicits.noop[IO]

  inline val migrationsPackage = "skunk.migrate.testmigrations"

  val migrations: List[Migration[IO]] = List(
    `20220728115157_TestOne`,
    `20220808102241_TestTwo`,
    `20220810131910_TestThree`,
  )

  val sharedDB: Resource[IO, Resource[IO, Session[IO]]] =
    Session.pooled[IO](
      host = "localhost",
      port = 5432,
      user = "postgres",
      database = "postgres",
      max = 1,
    )

  val genTempSchemaName: IO[String] =
    for
      rand <- Random.scalaUtilRandom[IO]
      chars <- rand.nextAlphaNumeric.replicateA(5)
    yield s"test_${chars.mkString}"

  def makeTempSchema(db: Resource[IO, Session[IO]]): Resource[IO, Resource[IO, Session[IO]]] =
    Resource
      .make {
        for
          schemaName <- genTempSchemaName
          _ <- db.use(_.execute(sql"CREATE SCHEMA #$schemaName".command))
        yield schemaName
      } { schemaName =>
        db.use(_.execute(sql"DROP SCHEMA #$schemaName CASCADE".command)).void
      }
      .map { schemaName =>
        db.evalTap { session =>
          session.execute(sql"SET search_path TO #$schemaName".command)
        }
      }

  val tempDB: Resource[IO, Resource[IO, Session[IO]]] =
    sharedDB >>= makeTempSchema

  test("discoverMigrations") {
    assertEquals(
      Migrator.discoverMigrations[IO](migrationsPackage),
      migrations,
    )
  }

  test("apply(db, pkg)") {
    val migrator = Migrator(sharedDB.flatten, migrationsPackage)
    assertEquals(migrator.migrations, migrations)
  }

  test("upAll, downAll, upAll") {
    tempDB.use { db =>
      val migrator = Migrator(db, migrations)

      val countFoo: IO[Long] =
        db.use { s =>
          s.unique(sql"SELECT COUNT(*) FROM foo".query(int8))
        }

      val assertAllUp: IO[Unit] =
        for
          _ <- assertIO(migrator.version, migrations.last.version.some)
          _ <- assertIO(countFoo, 0L)
        yield ()

      val assertAllDown: IO[Unit] =
        for
          _ <- assertIO(migrator.version, None)
          _ <- interceptIO[PostgresErrorException](countFoo)
        yield ()

      for
        _ <- assertIO(migrator.version, None)

        _ <- migrator.upAll
        _ <- assertAllUp

        _ <- migrator.downAll
        _ <- assertAllDown

        _ <- migrator.upAll
        _ <- assertAllUp
      yield ()
    }
  }
