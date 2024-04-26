package dev.mongocamp.driver.mongodb.database

import com.typesafe.config.{ Config, ConfigFactory }

trait ConfigHelper {
  val conf: Config = ConfigFactory.load()

  def stringConfig(configPath: String, key: String, default: String = ""): Option[String] = {
    if (conf.hasPath("%s.%s".format(configPath, key))) {
      val str = conf.getString("%s.%s".format(configPath, key))
      if (str.nonEmpty) {
        Some(str)
      }
      else {
        None
      }
    }
    else if (default.nonEmpty) {
      Some(default)
    }
    else {
      None
    }
  }

  def intConfig(configPath: String, key: String, default: Int = 0): Int = {
    if (conf.hasPath("%s.%s".format(configPath, key))) {
      conf.getInt("%s.%s".format(configPath, key))
    }
    else {
      default
    }
  }

  def booleanConfig(configPath: String, key: String, default: Boolean = false): Boolean = {
    if (conf.hasPath("%s.%s".format(configPath, key))) {
      conf.getBoolean("%s.%s".format(configPath, key))
    }
    else {
      default
    }
  }
}
