package skunk.migrate.testmigrations

import cats.Eval
import skunk.Session
import skunk.migrate.ReversibleMigration
import skunk.migrate.Version

object `2022-07-28T11:51:57Z__TestOne` extends ReversibleMigration[Eval] derives Version:
  def up(session: Session[Eval]): Eval[Unit] = Eval.Unit
  def down(session: Session[Eval]): Eval[Unit] = Eval.Unit
