package dev.mongocamp.driver.mongodb.server

import munit.FunSuite

class ServerConfigSuite extends FunSuite {

  test("ServerConfig should be created") {
    val config = ServerConfig()
    assertEquals(config.host, "localhost")
    assertEquals(config.port, 28018)
    assertEquals(config.serverName, "local-mongodb-server")
  }

  test("ServerConfig should be created from config path") {
    val config = ServerConfig.fromPath("unit.test.local.mongo.server")
    assertEquals(config.host, "localhost")
    assertEquals(config.port, 28028)
    assertEquals(config.serverName, "local-unit-test-server")
    assert(!config.h2BackendConfig.get.inMemory)
    assert(config.h2BackendConfig.get.path.isEmpty)
  }

}
