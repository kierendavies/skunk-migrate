package skunk.migrate.testmigrations

import cats.effect.IO
import skunk.Session
import skunk.migrate.ReversibleMigration
import skunk.migrate.Version

case object `20220808102241_TestTwo` extends ReversibleMigration[IO] derives Version:
  def up(session: Session[IO]): IO[Unit] = IO.unit
  def down(session: Session[IO]): IO[Unit] = IO.unit
