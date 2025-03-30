package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb.database.MongoPoolOptions._

case class MongoPoolOptions(
  maxConnectionIdleTime: Int = DefaultMaxConnectionIdleTime,
  maxSize: Int = DefaultMaxSize,
  minSize: Int = DefaultMinSize,
  maintenanceInitialDelay: Int = DefaultMaintenanceInitialDelay
) {}

object MongoPoolOptions {
  val DefaultMaxConnectionIdleTime   = 60
  val DefaultMaxSize                 = 50
  val DefaultMinSize                 = 0
  val DefaultMaintenanceInitialDelay = 0
}
