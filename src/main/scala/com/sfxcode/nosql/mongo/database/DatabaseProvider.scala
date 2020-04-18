package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.bson.codecs.CustomCodecProvider
import org.bson.codecs.configuration.CodecRegistries.{ fromProviders, fromRegistries }
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala._

import scala.collection.mutable

class DatabaseProvider(config: MongoConfig, registry: CodecRegistry) extends Serializable {
  private val cachedDatabaseMap = new mutable.HashMap[String, MongoDatabase]()
  private val cachedMongoDAOMap = new mutable.HashMap[String, MongoDAO[Document]]()

  private var cachedClient: Option[MongoClient] = None

  def client: MongoClient = {
    if (isClosed) {
      cachedDatabaseMap.clear()
      cachedMongoDAOMap.clear()
      cachedClient = Some(MongoClient(config.clientSettings))
    }
    cachedClient.get
  }

  def isClosed: Boolean = cachedClient.isEmpty

  def closeClient(): Unit = {
    client.close()
    cachedClient = None
  }

  def dropDatabase(databaseName: String): SingleObservable[Void] = database(databaseName).drop()

  def database(databaseName: String = config.database): MongoDatabase = {
    if (!cachedDatabaseMap.contains(databaseName)) {
      cachedDatabaseMap.put(databaseName, client.getDatabase(databaseName).withCodecRegistry(registry))
    }
    cachedDatabaseMap(databaseName)
  }

  def collection(collectionName: String): MongoCollection[Document] =
    dao(collectionName).collection

  def dao(collectionName: String): MongoDAO[Document] = {
    if (!cachedMongoDAOMap.contains(collectionName)) {
      cachedMongoDAOMap.put(collectionName, DocumentDao(this, collectionName))
    }
    cachedMongoDAOMap(collectionName)
  }

  def usedDatabaseNames(): List[String] = cachedDatabaseMap.keys.toList

  def databaseNames(): Observable[String] = client.listDatabaseNames()

  def usedCollectionNames(): List[String] = cachedMongoDAOMap.keys.toList

  def collectionNames(name: String = config.database): Observable[String] =
    database(name).listCollectionNames()

  case class DocumentDao(provider: DatabaseProvider, collectionName: String)
      extends MongoDAO[Document](this, collectionName)

}

object DatabaseProvider {
  val CollectionSeparator = ":"

  private val CustomRegistry = fromProviders(CustomCodecProvider())

  private val codecRegistry: CodecRegistry =
    fromRegistries(CustomRegistry, DEFAULT_CODEC_REGISTRY)

  def apply(config: MongoConfig, registry: CodecRegistry = codecRegistry): DatabaseProvider =
    new DatabaseProvider(config, fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY))

  def fromPath(configPath: String = MongoConfig.DefaultConfigPathPrefix,
               registry: CodecRegistry = codecRegistry): DatabaseProvider =
    apply(MongoConfig.fromPath(configPath), fromRegistries(registry, CustomRegistry, DEFAULT_CODEC_REGISTRY))

}
