package dev.mongocamp.driver.mongodb.server

object ServerBackend extends Enumeration {
  type ServerBackend = Value
  val Memory, H2 = Value
}
