package dev.mongocamp.driver.mongodb.database

object TopologyType extends Enumeration {
  type TopologyType = Value
  val Sharded, ReplicaSet, Standalone = Value
}
