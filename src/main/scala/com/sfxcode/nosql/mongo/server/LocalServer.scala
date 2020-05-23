package com.sfxcode.nosql.mongo.server

import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.memory.MemoryBackend

case class LocalServer(serverConfig: ServerConfig = ServerConfig()) {
  private val server = new MongoServer(new MemoryBackend())

  server.bind(serverConfig.host, serverConfig.port)

  def name: String = serverConfig.name

  def shutdown(): Unit = server.shutdown()

}
