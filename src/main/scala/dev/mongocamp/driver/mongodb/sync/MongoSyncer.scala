package dev.mongocamp.driver.mongodb.sync

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.database.{ DatabaseProvider, MongoConfig }
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.mongodb.scala.bson.codecs.Macros._

import scala.collection.mutable

case class MongoSyncer(
    sourceConfig: MongoConfig,
    targetConfig: MongoConfig,
    syncOperations: List[MongoSyncOperation] = List()
) {
  // todo: check if this is correct
//  private val registry     = fromProviders(classOf[MongoSyncResult])
  private val operationMap = new mutable.HashMap[String, MongoSyncOperation]()

  val source: DatabaseProvider = DatabaseProvider(sourceConfig)
  val target: DatabaseProvider = DatabaseProvider(targetConfig)

  object MongoSyncResultDAO extends MongoDAO[MongoSyncResult](source, MongoSyncOperation.SyncLogTableName)

  var terminated = false

  syncOperations.foreach(operation => addOperation(operation))

  def addOperation(operation: MongoSyncOperation): Option[MongoSyncOperation] =
    operationMap.put(operation.collectionName, operation)

  def sync(collectionName: String): List[MongoSyncResult] = {
    if (terminated)
      throw MongoSyncException("MongoSyncer already terminated")

    val result = operationMap
      .get(collectionName)
      .map(op => op.excecute(source, target))
      .getOrElse(List(MongoSyncResult(collectionName)))

    if (MongoSyncOperation.WriteSyncLogOnMaster)
      MongoSyncResultDAO.insertMany(result).results()
    result
  }

  def syncAll(): List[MongoSyncResult] = operationMap.keys.flatMap(key => sync(key)).toList

  def terminate(): Unit = {
    source.closeClient()
    target.closeClient()
    terminated = true
  }

}

object MongoSyncer {

  def fromPath(sourceConfigPath: String, targetConfigPath: String): MongoSyncer = {
    val sourceConfig = MongoConfig.fromPath(sourceConfigPath)
    val targetConfig = MongoConfig.fromPath(targetConfigPath)
    MongoSyncer(sourceConfig, targetConfig)
  }
}
