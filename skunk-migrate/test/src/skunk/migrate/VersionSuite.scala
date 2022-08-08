package skunk.migrate

import munit.FunSuite
import skunk.migrate.testmigrations.*

class VersionSuite extends FunSuite:
  test("derive from class name") {
    assertEquals(`2022-07-28T11:51:57Z__TestOne`.version, 1659009117L)
  }
