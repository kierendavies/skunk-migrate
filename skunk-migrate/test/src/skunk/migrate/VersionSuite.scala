package skunk.migrate

import munit.FunSuite
import skunk.migrate.testmigrations.*

class VersionSuite extends FunSuite:
  test("derive from class name") {
    assertEquals(`20220728115157_TestOne`.version, 1659009117L)
  }
