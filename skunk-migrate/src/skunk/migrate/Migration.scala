package skunk.migrate

import skunk.Session

sealed trait Migration[F[_]]:
  def up(session: Session[F]): F[Unit]

trait IrreversibleMigration[F[_]] extends Migration[F]

trait ReversibleMigration[F[_]] extends Migration[F]:
  def down(session: Session[F]): F[Unit]

object Migration:
  extension [M <: Migration[?]](migration: M)(using version: Version[M])
    def timestamp: Long = version.timestamp(migration)
    def name: String = version.name(migration)
