package dev.mongocamp.driver.mongodb.database

import munit.FunSuite
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration.SECONDS

class ConfigHelperSuite extends FunSuite {

  object Helper extends ConfigHelper

  test("durationConfig should read a plain number as milliseconds") {
    assertEquals(Helper.durationConfig("config.test.duration", "plainMillis", FiniteDuration(1, SECONDS)), FiniteDuration(5, SECONDS))
  }

  test("durationConfig should read a value with an explicit unit suffix") {
    assertEquals(Helper.durationConfig("config.test.duration", "withUnit", FiniteDuration(1, SECONDS)), FiniteDuration(5, SECONDS))
  }

  test("durationConfig should fall back to the default when the path is missing") {
    assertEquals(Helper.durationConfig("config.test.duration", "missing", FiniteDuration(42, SECONDS)), FiniteDuration(42, SECONDS))
  }
}
