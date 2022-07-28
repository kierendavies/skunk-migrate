package skunk.migrate

import cats.Eval
import munit.FunSuite
import skunk.Session
import skunk.Transaction

class VersionSuite extends FunSuite:
  test("derive from class name") {
    object `2022-07-28T11:51:57Z__TestMigration` extends ReversibleMigration[Eval] derives Version.FromClass:
      def up(s: Session[Eval], xa: Transaction[Eval]): Eval[Unit] = Eval.Unit
      def down(s: Session[Eval], xa: Transaction[Eval]): Eval[Unit] = Eval.Unit

    val m = `2022-07-28T11:51:57Z__TestMigration`

    assert(clue(m.timestamp) == 1659009117)
    assert(clue(m.name) == "TestMigration")
  }
