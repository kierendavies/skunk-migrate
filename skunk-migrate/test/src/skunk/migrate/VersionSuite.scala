package skunk.migrate

import munit.FunSuite

import testmigrations.`2022-07-28T11:51:57Z__TestMigration`

class VersionSuite extends FunSuite:
  test("derive from class name") {
    assert(clue(`2022-07-28T11:51:57Z__TestMigration`.timestamp) == 1659009117)
    assert(clue(`2022-07-28T11:51:57Z__TestMigration`.name) == "TestMigration")
  }
