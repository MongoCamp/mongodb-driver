package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.bson.codecs.CustomCodecProvider
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ MongoClient, MongoDatabase, Observable }

class DatabaseProvider(databaseName: String, registry: CodecRegistry, client: MongoClient) {

  val database: MongoDatabase = client.getDatabase(databaseName).withCodecRegistry(registry)

  def listDatabaseNames(): Observable[String] = client.listDatabaseNames()

}

object DatabaseProvider {
  private val CustomRegistry = fromProviders(CustomCodecProvider())
  private val codecRegistry: CodecRegistry = fromRegistries(CustomRegistry, DEFAULT_CODEC_REGISTRY)

  def apply(databaseName: String): DatabaseProvider = {
    new DatabaseProvider(databaseName, codecRegistry, MongoClient())
  }

  def apply(databaseName: String, registry: CodecRegistry, client: MongoClient = MongoClient()): DatabaseProvider = {
    new DatabaseProvider(databaseName, fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY), client)
  }

}
