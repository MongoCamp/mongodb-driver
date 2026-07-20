package dev.mongocamp.driver.mongodb.database

import com.mongodb.connection.ClusterSettings
import com.mongodb.connection.ConnectionPoolSettings
import com.mongodb.event.CommandListener
import com.mongodb.event.ConnectionPoolListener
import com.mongodb.MongoCompressor
import com.mongodb.MongoCredential.createCredential
import dev.mongocamp.driver.mongodb.database.MongoConfig.CompressionSnappy
import dev.mongocamp.driver.mongodb.database.MongoConfig.CompressionZlib
import dev.mongocamp.driver.mongodb.database.MongoConfig.CompressionZstd
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultApplicationName
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultAuthenticationDatabaseName
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultHost
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultPort
import java.util.concurrent.TimeUnit
import org.mongodb.scala.connection._
import org.mongodb.scala.MongoClientSettings
import org.mongodb.scala.MongoCredential
import org.mongodb.scala.ServerAddress
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

case class MongoConfig(
  database: String,
  host: String = DefaultHost,
  port: Int = DefaultPort,
  var applicationName: String = DefaultApplicationName,
  userName: Option[String] = None,
  password: Option[String] = None,
  authDatabase: String = DefaultAuthenticationDatabaseName,
  poolOptions: MongoPoolOptions = MongoPoolOptions(),
  compressors: List[String] = List.empty,
  connectionPoolListener: List[ConnectionPoolListener] = List.empty,
  commandListener: List[CommandListener] = List.empty,
  customClientSettings: Option[MongoClientSettings] = None,
  serverAddressList: List[ServerAddress] = List.empty
) {

  lazy val clientSettings: MongoClientSettings = {
    if (customClientSettings.isDefined) {
      customClientSettings.get
    }
    else {
      val clusterSettings: ClusterSettings = ClusterSettings.builder().hosts(fullServerAddressList.asJava).build()

      val connectionPoolSettingsBuilder = ConnectionPoolSettings
        .builder()
        .maxConnectionIdleTime(poolOptions.maxConnectionIdleTime, TimeUnit.SECONDS)
        .maxSize(poolOptions.maxSize)
        .minSize(poolOptions.minSize)
        .maintenanceInitialDelay(poolOptions.maintenanceInitialDelay, TimeUnit.SECONDS)

      connectionPoolListener.foreach(
        listener => connectionPoolSettingsBuilder.addConnectionPoolListener(listener)
      )

      val connectionPoolSettings = connectionPoolSettingsBuilder.build()

      val compressorList = new ArrayBuffer[MongoCompressor]()
      compressors.foreach(
        compression => {
          if (CompressionSnappy.equalsIgnoreCase(compression)) {
            compressorList.+=(MongoCompressor.createSnappyCompressor())
          }
          else if (CompressionZlib.equalsIgnoreCase(compression)) {
            compressorList.+=(MongoCompressor.createZlibCompressor())
          }
          else if (CompressionZstd.equalsIgnoreCase(compression)) {
            compressorList.+=(MongoCompressor.createZstdCompressor())
          }
        }
      )

      val builder = MongoClientSettings
        .builder()
        .applicationName(applicationName)
        .applyToConnectionPoolSettings(
          (b: ConnectionPoolSettings.Builder) => b.applySettings(connectionPoolSettings)
        )
        .applyToClusterSettings(
          (b: ClusterSettings.Builder) => b.applySettings(clusterSettings)
        )
        .compressorList(compressorList.asJava)

      commandListener.foreach(
        listener => builder.addCommandListener(listener)
      )

      if (userName.isDefined && password.isDefined) {
        val credential: MongoCredential = createCredential(userName.get, authDatabase, password.get.toCharArray)
        builder.credential(credential).build()
      }
      else {
        builder.build()
      }
    }
  }

  def fullServerAddressList: List[ServerAddress] = {
    val hostClientBasedList = List(new ServerAddress(host, port))
    val internalServerAddressList = if (serverAddressList.nonEmpty) {
      hostClientBasedList ++ serverAddressList
    }
    else {
      hostClientBasedList
    }
    internalServerAddressList.distinct
  }
}

object MongoConfig extends ConfigHelper {
  val DefaultHost                       = "127.0.0.1"
  val DefaultPort                       = 27017
  val DefaultAuthenticationDatabaseName = "admin"
  val DefaultApplicationName            = "mongocampdb-app"

  val DefaultPoolMaxConnectionIdleTime   = 60
  val DefaultPoolMaxSize                 = 50
  val DefaultPoolMinSize                 = 0
  val DefaultPoolMaxWaitQueueSize        = 500
  val DefaultPoolMaintenanceInitialDelay = 0

  val CompressionSnappy = "snappy"
  val CompressionZlib   = "zlib"
  val CompressionZstd   = "zstd"

  val DefaultConfigPathPrefix = "mongodb"

  def fromPath(configPath: String = DefaultConfigPathPrefix): MongoConfig = {

    def poolOptionsConfig(key: String, default: Int): Int = {
      if (conf.hasPath("%s.pool.%s".format(configPath, key))) {
        conf.getInt("%s.pool.%s".format(configPath, key))
      }
      else {
        default
      }
    }

    val port: Int = intConfig(configPath, "port", DefaultPort)

    val compressors: List[String] = {
      if (conf.hasPath("%s.compressors".format(configPath))) {
        conf.getStringList("%s.compressors".format(configPath)).asScala.toList
      }
      else {
        List()
      }
    }

    val host            = stringConfig(configPath, "host", DefaultHost).get
    val database        = stringConfig(configPath, "database").get
    val userName        = stringConfig(configPath, "userName")
    val password        = stringConfig(configPath, "password")
    val authDatabase    = stringConfig(configPath, "authDatabase", DefaultAuthenticationDatabaseName).get
    val applicationName = stringConfig(configPath, "applicationName", DefaultApplicationName).get

    val poolOptions = MongoPoolOptions(
      poolOptionsConfig("maxConnectionIdleTime", DefaultPoolMaxConnectionIdleTime),
      poolOptionsConfig("maxSize", DefaultPoolMaxSize),
      poolOptionsConfig("minSize", DefaultPoolMinSize),
      poolOptionsConfig("maintenanceInitialDelay", DefaultPoolMaintenanceInitialDelay)
    )

    val additionalServerAddresses: List[ServerAddress] = {
      if (conf.hasPath("%s.additionalServerAddresses".format(configPath))) {
        conf.getStringList("%s.additionalServerAddresses".format(configPath)).asScala.toList.map(extractServerAddressFromString)
      }
      else {
        List.empty
      }
    }

    MongoConfig(
      database,
      host,
      port,
      applicationName,
      userName,
      password,
      authDatabase,
      poolOptions,
      compressors,
      serverAddressList = additionalServerAddresses
    )
  }

  def extractServerAddressFromString(serverString: String): ServerAddress = {
    val hostParts = serverString.split(":")
    if (hostParts.length == 2) {
      ServerAddress(hostParts(0), hostParts(1).toInt)
    }
    else {
      new ServerAddress(hostParts(0))
    }
  }

}
