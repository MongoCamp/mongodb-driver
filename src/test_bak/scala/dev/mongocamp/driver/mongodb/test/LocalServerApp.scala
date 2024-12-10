package dev.mongocamp.driver.mongodb.test

import dev.mongocamp.driver.mongodb.server.LocalServer

// used fo LocalServer testing with external Tools
object LocalServerApp extends App {
  UniversityDatabase.LocalTestServer = LocalServer.fromPath("unit.test.local.mongo.server")

  while (true) {}

}
