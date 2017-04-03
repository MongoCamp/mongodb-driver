package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.bson.codecs.CustomCodecProvider
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.{ MongoClient, MongoDatabase }

case class DatabaseProvider(databaseName: String, registry: CodecRegistry, client: MongoClient = MongoClient()) {

  import org.bson.codecs.configuration.CodecRegistries.fromRegistries
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

  private val CustomRegistry = fromProviders(CustomCodecProvider())

  private val codecRegistry: CodecRegistry = fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY)

  val database: MongoDatabase = client.getDatabase(databaseName).withCodecRegistry(codecRegistry)

}