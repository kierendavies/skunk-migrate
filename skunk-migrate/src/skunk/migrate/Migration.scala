package skunk.migrate

import skunk.Session

sealed trait Migration[F[_]]:
  protected def derived$Version: Version[this.type]
  def version: Long = derived$Version.underlying

  def up(session: Session[F]): F[Unit]

trait IrreversibleMigration[F[_]] extends Migration[F]

trait ReversibleMigration[F[_]] extends Migration[F]:
  def down(session: Session[F]): F[Unit]
