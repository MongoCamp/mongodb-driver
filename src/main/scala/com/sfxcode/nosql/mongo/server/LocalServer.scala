package com.sfxcode.nosql.mongo.server

import better.files.File
import com.sfxcode.nosql.mongo.server.ServerConfig.DefaultServerConfigPathPrefix
import de.bwaldvogel.mongo.MongoServer
import de.bwaldvogel.mongo.backend.h2.H2Backend
import de.bwaldvogel.mongo.backend.memory.MemoryBackend

case class LocalServer(serverConfig: ServerConfig = ServerConfig()) {
  private var h2Path = "undefined"

  private val server: MongoServer = {
    if (ServerBackend.H2 == serverConfig.backend)
      if (serverConfig.h2BackendConfig.isDefined && !serverConfig.h2BackendConfig.get.inMemory) {
        if (serverConfig.h2BackendConfig.get.path.isDefined)
          h2Path = serverConfig.h2BackendConfig.get.path.get
        else
          h2Path = File.temporaryFile().get().path.toString
        createH2Server(h2Path)
      }
      else
        createH2InMemoryServer
    else
      createInMemoryServer
  }

  server.bind(serverConfig.host, serverConfig.port)

  def name: String = serverConfig.serverName

  def h2FilePath: String = h2Path

  def shutdown(): Unit = server.shutdown()

  private def createInMemoryServer: MongoServer =
    new MongoServer(new MemoryBackend())

  private def createH2InMemoryServer: MongoServer =
    new MongoServer(H2Backend.inMemory())

  private def createH2Server(path: String): MongoServer =
    new MongoServer(new H2Backend(path))

}

object LocalServer {

  def fromPath(configPath: String = DefaultServerConfigPathPrefix): LocalServer =
    LocalServer(ServerConfig.fromPath(configPath))
}
