package dev.mongocamp.driver.mongodb.server

case class H2BackendConfig(inMemory: Boolean = true, path: Option[String] = None)
