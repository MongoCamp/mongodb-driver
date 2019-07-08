package com.sfxcode.nosql.mongo.database

import java.util.concurrent.TimeUnit

import com.mongodb.MongoCredential.createCredential
import com.sfxcode.nosql.mongo.bson.codecs.CustomCodecProvider
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.connection.{ClusterSettings, ConnectionPoolSettings}
import org.mongodb.scala.{MongoClient, MongoClientSettings, MongoCredential, MongoDatabase, Observable, ServerAddress}

import scala.collection.JavaConverters._

class DatabaseProvider(databaseName: String, registry: CodecRegistry, client: MongoClient) {

  val database: MongoDatabase =
    client.getDatabase(databaseName).withCodecRegistry(registry)

  def listDatabaseNames(): Observable[String] = client.listDatabaseNames()

}

object DatabaseProvider {

  private val CustomRegistry = fromProviders(CustomCodecProvider())

  private val codecRegistry: CodecRegistry =
    fromRegistries(CustomRegistry, DEFAULT_CODEC_REGISTRY)

  def apply(databaseName: String, registry: CodecRegistry = codecRegistry, client: MongoClient = MongoClient()): DatabaseProvider =
    new DatabaseProvider(databaseName, fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY), client)

  def fromConfigPath(configPath:String = MongoConfig.DefaultConfigPathPrefix, registry: CodecRegistry = codecRegistry):DatabaseProvider = {
    fromConfig(MongoConfig.fromConfigPath(configPath), fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY))
  }

  def fromConfig(mongoConfig: MongoConfig, registry: CodecRegistry = codecRegistry):DatabaseProvider = {

    val clusterSettings: ClusterSettings =
      ClusterSettings.builder().hosts(List(new ServerAddress(mongoConfig.host, mongoConfig.port)).asJava).build()

    val connectionPoolSettings = ConnectionPoolSettings
      .builder()
      .maxConnectionIdleTime(mongoConfig.maxConnectionIdleTime, TimeUnit.SECONDS)
      .maxSize(mongoConfig.maxSize)
      .minSize(mongoConfig.minSize)
      .maxWaitQueueSize(mongoConfig.maxWaitQueueSize)
      .maintenanceInitialDelay(mongoConfig.maintenanceInitialDelay, TimeUnit.SECONDS)
      .build()

    val builder = MongoClientSettings
      .builder()
      .applyToConnectionPoolSettings(
        (b: com.mongodb.connection.ConnectionPoolSettings.Builder) => b.applySettings(connectionPoolSettings)
      )
      .applyToClusterSettings((b: com.mongodb.connection.ClusterSettings.Builder) => b.applySettings(clusterSettings))

    if (mongoConfig.user.isDefined && mongoConfig.password.isDefined)  {
      val credential: MongoCredential = createCredential(mongoConfig.user.get,
        mongoConfig.authDatabase, mongoConfig.password.get.toCharArray)

      val clientSettings: MongoClientSettings = builder.credential(credential).build()
      new DatabaseProvider(mongoConfig.database, fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY), MongoClient(clientSettings))
    }
    else {
      val clientSettings: MongoClientSettings = builder.build()
      new DatabaseProvider(mongoConfig.database, fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY), MongoClient(clientSettings))
    }
  }
}
