package skunk.migrate.testmigrations

import cats.effect.IO
import skunk.Session
import skunk.migrate.ReversibleMigration
import skunk.migrate.Version
import skunk.syntax.all.*

case object `2022-07-28T11:51:57Z__TestOne` extends ReversibleMigration[IO] derives Version:
  def up(session: Session[IO]): IO[Unit] =
    session
      .execute(sql"CREATE TABLE foo (bar character varying)".command)
      .void

  def down(session: Session[IO]): IO[Unit] =
    session
      .execute(sql"DROP TABLE foo".command)
      .void
