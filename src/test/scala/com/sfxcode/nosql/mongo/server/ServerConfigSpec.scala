package com.sfxcode.nosql.mongo.server

import org.specs2.mutable.Specification

class ServerConfigSpec extends Specification {

  sequential

  "ServerConfig" should {

    "be created" in {
      val config = ServerConfig()
      config.host mustEqual "localhost"
      config.port mustEqual 28018
      config.serverName mustEqual "local-mongo-server"
    }

    "be created from config path" in {
      val config = ServerConfig.fromPath("unit.test.local.mongo.server")
      config.host mustEqual "localhost"
      config.port mustEqual 28028
      config.serverName mustEqual "local-unit-test-server"
      config.h2BackendConfig.get.inMemory must beFalse
      config.h2BackendConfig.get.path mustEqual ""
    }
  }
}
