package dev.mongocamp.driver.mongodb.sync

object SyncDirection extends Enumeration {
  type SyncDirection = Value
  val SourceToTarget, TargetToSource, TwoWay = Value
}
