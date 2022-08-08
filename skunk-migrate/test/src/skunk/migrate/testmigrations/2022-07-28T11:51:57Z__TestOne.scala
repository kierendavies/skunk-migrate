package skunk.migrate.testmigrations

import cats.Eval
import skunk.Session
import skunk.Transaction
import skunk.migrate.ReversibleMigration
import skunk.migrate.Version

object `2022-07-28T11:51:57Z__TestOne` extends ReversibleMigration[Eval] derives Version.FromClass:
  def up(s: Session[Eval], xa: Transaction[Eval]): Eval[Unit] = Eval.Unit
  def down(s: Session[Eval], xa: Transaction[Eval]): Eval[Unit] = Eval.Unit
