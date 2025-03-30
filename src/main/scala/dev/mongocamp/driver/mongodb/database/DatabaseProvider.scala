package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.json._
import org.mongodb.scala._
import org.mongodb.scala.gridfs.GridFSBucket
import scala.collection.mutable
import scala.reflect.ClassTag

class DatabaseProvider(val config: MongoConfig) extends Serializable {
  private val cachedDatabaseMap                 = new mutable.HashMap[String, MongoDatabase]()
  private val cachedMongoDAOMap                 = new mutable.HashMap[String, MongoDAO[Document]]()
  private var cachedClient: Option[MongoClient] = None

  private var defaultDatabaseName: String = config.database

  def DefaultDatabaseName: String = defaultDatabaseName

  def connectionString: String = {
    s"mongodb://${config.host}:${config.port}/${config.database}"
  }

  def setDefaultDatabaseName(databaseName: String): Unit = {
    defaultDatabaseName = databaseName
  }

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

  def databases: ListDatabasesObservable[Document] = client.listDatabases()

  def databaseInfos: List[DatabaseInfo] = databases
    .resultList()
    .map(
      doc => DatabaseInfo(doc)
    )
    .sortBy(_.name)

  def databaseNames: List[String] = databaseInfos.map(
    info => info.name
  )

  def dropDatabase(databaseName: String = DefaultDatabaseName): SingleObservable[Unit] = database(databaseName).drop()

  def compactDatabase(databaseName: String = DefaultDatabaseName, maxWaitPerCollection: Int = DefaultMaxWait): List[CompactResult] = {
    collectionNames(databaseName).flatMap(
      collectionName => dao(collectionName).compact.result(maxWaitPerCollection)
    )
  }

  def compact(maxWaitPerCollection: Int = DefaultMaxWait): List[CompactResult] = {
    databaseNames.flatMap(
      database =>
        try
          collectionNames(database).flatMap(
            collectionName => dao(collectionName).compact.result(maxWaitPerCollection)
          )
        catch {
          case e: MongoCommandException => List()
        }
    )
  }

  def database(databaseName: String = DefaultDatabaseName): MongoDatabase = {
    if (!cachedDatabaseMap.contains(databaseName)) {
      cachedDatabaseMap.put(databaseName, client.getDatabase(databaseName))
    }
    cachedDatabaseMap(databaseName)
  }

  def addChangeObserver(observer: ChangeObserver[Document], databaseName: String = DefaultDatabaseName): ChangeObserver[Document] = {
    database(databaseName).watch().subscribe(observer)
    observer
  }

  def collections(databaseName: String = DefaultDatabaseName): ListCollectionsObservable[Document] = {
    database(databaseName).listCollections()
  }

  def collectionInfos(databaseName: String = DefaultDatabaseName): List[CollectionInfo] = {
    collections(databaseName)
      .resultList()
      .map(
        doc => CollectionInfo(doc)
      )
      .sortBy(_.name)
  }

  def collectionNames(databaseName: String = DefaultDatabaseName): List[String] = {
    collectionInfos(databaseName).map(
      info => info.name
    )
  }

  def runCommand(document: Document, databaseName: String = DefaultDatabaseName): SingleObservable[Document] = {
    database(databaseName).runCommand(document)
  }

  def collectionStatus(collectionName: String, databaseName: String = DefaultDatabaseName): Observable[CollectionStatus] = {
    runCommand(Map("collStats" -> collectionName), databaseName).map(
      document => CollectionStatus(document)
    )
  }

  def collection(collectionName: String): MongoCollection[Document] =
    if (collectionName.contains(DatabaseProvider.CollectionSeparator)) {
      val newDatabaseName: String   = guessDatabaseName(collectionName)
      val newCollectionName: String = guessName(collectionName)
      database(newDatabaseName).getCollection(newCollectionName)
    }
    else {
      database().getCollection(collectionName)
    }

  def guessDatabaseName(maybeSeparatedName: String): String = {
    if (maybeSeparatedName.contains(DatabaseProvider.CollectionSeparator)) {
      maybeSeparatedName.substring(0, maybeSeparatedName.indexOf(DatabaseProvider.CollectionSeparator))
    }
    else {
      DefaultDatabaseName
    }
  }

  def guessName(maybeSeparatedName: String): String = {
    if (maybeSeparatedName.contains(DatabaseProvider.CollectionSeparator)) {
      maybeSeparatedName.substring(maybeSeparatedName.indexOf(DatabaseProvider.CollectionSeparator) + 1)
    }
    else {
      maybeSeparatedName
    }
  }

  def bucket(bucketName: String): GridFSBucket = {
    if (bucketName.contains(DatabaseProvider.CollectionSeparator)) {
      val newDatabaseName = guessDatabaseName(bucketName)
      val newBucketName   = guessName(bucketName)
      GridFSBucket(database(newDatabaseName), newBucketName)
    }
    else {
      GridFSBucket(database(), bucketName)
    }
  }

  def dao(collectionName: String): MongoDAO[Document] = {
    if (!cachedMongoDAOMap.contains(collectionName)) {
      cachedMongoDAOMap.put(collectionName, DocumentDao(this, collectionName))
    }
    cachedMongoDAOMap(collectionName)
  }

  def cachedDatabaseNames(): List[String] = cachedDatabaseMap.keys.toList

  def cachedCollectionNames(): List[String] = cachedMongoDAOMap.keys.toList

  case class DocumentDao(provider: DatabaseProvider, collectionName: String) extends MongoDAO[Document](this, collectionName)

}

object DatabaseProvider {
  val ObjectIdKey         = "_id"
  val CollectionSeparator = ":"

  def apply(config: MongoConfig): DatabaseProvider = {
    new DatabaseProvider(config)
  }

  def fromPath(configPath: String = MongoConfig.DefaultConfigPathPrefix): DatabaseProvider = {
    apply(MongoConfig.fromPath(configPath))
  }

}
