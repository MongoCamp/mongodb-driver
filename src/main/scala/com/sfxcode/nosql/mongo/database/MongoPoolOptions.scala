package com.sfxcode.nosql.mongo.database

import MongoPoolOptions._

case class MongoPoolOptions(
  maxConnectionIdleTime: Int = DefaultMaxConnectionIdleTime,
  maxSize: Int = DefaultMaxSize,
  minSize: Int = DefaultMinSize,
  maxWaitQueueSize: Int = DefaultMaxWaitQueueSize,
  maintenanceInitialDelay: Int = DefaultMaintenanceInitialDelay) {}

object MongoPoolOptions {
  val DefaultMaxConnectionIdleTime = 60
  val DefaultMaxSize = 50
  val DefaultMinSize = 0
  val DefaultMaxWaitQueueSize = 500
  val DefaultMaintenanceInitialDelay = 0
}
