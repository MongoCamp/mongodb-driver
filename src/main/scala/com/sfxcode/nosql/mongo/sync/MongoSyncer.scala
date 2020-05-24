package com.sfxcode.nosql.mongo.sync

import com.sfxcode.nosql.mongo.database.{DatabaseProvider, MongoConfig}

import scala.collection.mutable

case class MongoSyncer(sourceConfig: MongoConfig, targetConfig: MongoConfig) {
  val source: DatabaseProvider = DatabaseProvider(sourceConfig)
  val target: DatabaseProvider = DatabaseProvider(targetConfig)

  var terminated = false

  private val operationMap = new mutable.HashMap[String, MongoSyncOperation]()

  def addOperation(operation: MongoSyncOperation): Option[MongoSyncOperation] =
    operationMap.put(operation.collectionName, operation)

  def sync(collectionName: String): List[MongoSyncResult] = {
    if (terminated) {
      throw MongoSyncException("MongoSyncer already terminated")
    }
    operationMap
      .get(collectionName)
      .map(op => op.excecute(source, target))
      .getOrElse(List(MongoSyncResult(collectionName)))
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
