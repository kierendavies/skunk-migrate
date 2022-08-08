package skunk.migrate

import munit.FunSuite
import skunk.migrate.testmigrations.*

class VersionSuite extends FunSuite:
  test("derive from class name") {
    assert(clue(`2022-07-28T11:51:57Z__TestOne`.timestamp) == 1659009117)
    assert(clue(`2022-07-28T11:51:57Z__TestOne`.name) == "TestOne")
  }
