package skunk.migrate.testmigrations

import cats.Eval
import skunk.Session
import skunk.Transaction
import skunk.migrate.ReversibleMigration
import skunk.migrate.Version

object `2022-08-08T10:22:41Z__TestTwo` extends ReversibleMigration[Eval] derives Version.FromClass:
  def up(s: Session[Eval], xa: Transaction[Eval]): Eval[Unit] = Eval.Unit
  def down(s: Session[Eval], xa: Transaction[Eval]): Eval[Unit] = Eval.Unit
