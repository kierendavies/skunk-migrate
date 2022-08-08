package skunk.migrate

import cats.Eval
import munit.FunSuite
import skunk.migrate.testmigrations.*

class MigrateSuite extends FunSuite:
  test("discover migrations") {
    val discovered = Migrate.discoverMigrations[Eval]("skunk.migrate.testmigrations")

    assert(clue(discovered).contains(`2022-07-28T11:51:57Z__TestOne`))
    assert(clue(discovered).contains(`2022-08-08T10:22:41Z__TestTwo`))
  }
