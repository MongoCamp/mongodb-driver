package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.database.MongoConfig._
import com.typesafe.config.{Config, ConfigFactory}

case class MongoConfig(
                        host: String = DefaultHost,
                        port: Int = DefaultPort,
                        database: String,
                        user: Option[String] = None,
                        password: Option[String] = None,
                        authDatabase: String = DefaultAuthenticationDatabaseName,
                        options: Map[String, Int] = Map()) {

  def maxConnectionIdleTime: Int = options.getOrElse("maxConnectionIdleTime", DefaultMaxConnectionIdleTime)
  def maxSize: Int = options.getOrElse("maxSize", DefaultMaxSize)
  def minSize: Int = options.getOrElse("minSize", DefaultMinSize)
  def maxWaitQueueSize: Int = options.getOrElse("maxWaitQueueSize", DefaultMaxWaitQueueSize)
  def maintenanceInitialDelay: Int = options.getOrElse("maintenanceInitialDelay", DefaultMaintenanceInitialDelay)

}

object MongoConfig {
  val DefaultHost = "127.0.0.1"
  val DefaultPort = 27017
  val DefaultAuthenticationDatabaseName = "admin"

  val DefaultMaxConnectionIdleTime = 60
  val DefaultMaxSize = 50
  val DefaultMinSize = 0
  val DefaultMaxWaitQueueSize = 500
  val DefaultMaintenanceInitialDelay = 0

  val DefaultConfigPathPrefix = "mongo"

  val conf: Config = ConfigFactory.load()

  def fromConfigPath(configPath:String):MongoConfig = {
    def stringConfig(key:String, default:String = ""):Option[String] = {
      if (conf.hasPath("%s.%s".format(configPath, key))) {
        Some(conf.getString("%s.%s".format(configPath, key)))
      } else {
        if (default.nonEmpty) {
          Some(default)
        } else {
          None
        }
      }
    }

    def optionConfig(key:String, default:Int):Int = {
      if (conf.hasPath("%s.options.%s".format(configPath, key))) {
        conf.getInt("%s.options.%s".format(configPath, key))
      } else {
        default
      }
    }

    def portConfig:Int = {
      if (conf.hasPath("%s.port".format(configPath))) {
        conf.getInt("%s.port".format(configPath))
      } else {
        DefaultPort
      }
    }

    val host = stringConfig("host", DefaultHost).get
    val port = portConfig
    val database = stringConfig("database").get
    val user = stringConfig("user")
    val password = stringConfig("password")
    val authDatabase = stringConfig("authDatabase", DefaultAuthenticationDatabaseName).get

    val options = Map(
      "maxConnectionIdleTime" -> optionConfig("maxConnectionIdleTime", DefaultMaxConnectionIdleTime),
      "maxSize" -> optionConfig("maxSize", DefaultMaxSize),
      "minSize" -> optionConfig("minSize", DefaultMinSize),
      "maxWaitQueueSize" -> optionConfig("maxWaitQueueSize", DefaultMaxWaitQueueSize),
      "maintenanceInitialDelay" -> optionConfig("maintenanceInitialDelay", DefaultMaintenanceInitialDelay)
    )

    MongoConfig(host, port, database, user, password, authDatabase, options)
  }


}
