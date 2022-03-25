package dev.mongocamp.driver.mongodb.server

import dev.mongocamp.driver.mongodb.database.ConfigHelper
import dev.mongocamp.driver.mongodb.server.ServerBackend.ServerBackend

case class ServerConfig(
    serverName: String = ServerConfig.DefaultServerName,
    host: String = ServerConfig.DefaultHost,
    port: Int = ServerConfig.DefaultPort,
    backend: ServerBackend = ServerBackend.Memory,
    h2BackendConfig: Option[H2BackendConfig] = None
)

case class H2BackendConfig(inMemory: Boolean = true, path: Option[String] = None)

object ServerBackend extends Enumeration {
  type ServerBackend = Value
  val Memory, H2 = Value
}

object ServerConfig extends ConfigHelper {
  val DefaultServerConfigPathPrefix = "local.mongodb.server"

  val DefaultServerName = "local-mongodb-server"
  val DefaultHost       = "localhost"
  val DefaultPort       = 28018

  def serverBackendFromString(backendName: String): ServerBackend.Value =
    if (ServerBackend.H2.toString.toLowerCase.equals(backendName.toLowerCase))
      ServerBackend.H2
    else
      ServerBackend.Memory

  def fromPath(configPath: String = DefaultServerConfigPathPrefix): ServerConfig = {

    val name = stringConfig(configPath, "serverName", DefaultServerName).get
    val host = stringConfig(configPath, "host", DefaultHost).get
    val port = intConfig(configPath, "port", DefaultPort)

    val backend = stringConfig(configPath, "backend").get

    val serverBackend: ServerBackend = serverBackendFromString(backend)

    val h2BackendConfig: Option[H2BackendConfig] = {
      if (serverBackend == ServerBackend.H2 && conf.hasPath("%s.%s".format(configPath, "h2.inMemory"))) {
        val inMemory = booleanConfig(configPath, "h2.inMemory")
        val path     = stringConfig(configPath, "h2.path")
        Some(H2BackendConfig(inMemory, path))
      }
      else
        None
    }

    ServerConfig(name, host, port, serverBackend, h2BackendConfig)

  }
}
