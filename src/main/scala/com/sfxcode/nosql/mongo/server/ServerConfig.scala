package com.sfxcode.nosql.mongo.server

import ServerConfig._

case class ServerConfig(name: String = "MongoLocal", host: String = DefaultHost, port: Int = DefaultPort)

object ServerConfig {
  val DefaultHost = "localhost"
  val DefaultPort = 28018
}
