package com.sfxcode.nosql.mongo.test

import com.sfxcode.nosql.mongo.server.LocalServer

// used fo LocalServer testing with external Tools
object LocalServerApp extends App {
  UniversityDatabase.LocalTestServer = LocalServer.fromPath("unit.test.local.mongo.server")

  while (true) {}

}
