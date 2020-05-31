package com.sfxcode.nosql.mongo.sync

import java.util.Date

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.database.{ConfigHelper, DatabaseProvider}
import com.sfxcode.nosql.mongo.sync.SyncDirection.SyncDirection
import com.sfxcode.nosql.mongo.sync.SyncStrategy.SyncStrategy
import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.Document
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._

object SyncStrategy extends Enumeration {
  type SyncStrategy = Value
  val Replcace = Value
}

object SyncDirection extends Enumeration {
  type SyncDirection = Value
  val SourceToTarget, TargetToSource, TwoWay = Value
}

case class MongoSyncException(message: String) extends Exception(message)

case class MongoSyncOperation(
    collectionName: String,
    syncDirection: SyncDirection = SyncDirection.SourceToTarget,
    syncStrategy: SyncStrategy = SyncStrategy.Replcace,
    idColumnName: String = DatabaseProvider.ObjectIdKey
) extends LazyLogging
    with Filter {
  val includes = include(idColumnName, MongoSyncOperation.SyncColumnLastSync, MongoSyncOperation.SyncColumnLastUpdate)

  def excecute(source: DatabaseProvider, target: DatabaseProvider): List[MongoSyncResult] =
    try {
      val sourceInfos: Seq[Document] =
        source.dao(collectionName).find().projection(includes).results(MongoSyncOperation.MaxWait)
      val targetInfos: Seq[Document] =
        target.dao(collectionName).find().projection(includes).results(MongoSyncOperation.MaxWait)

      if (SyncDirection.SourceToTarget == syncDirection) {
        val diff = sourceInfos.diff(targetInfos)
        List(syncInternal(source, target, targetInfos.size, diff))
      }
      else if (SyncDirection.TargetToSource == syncDirection) {
        val diff = targetInfos.diff(sourceInfos)
        List(syncInternal(target, source, sourceInfos.size, diff))
      }
      else if (SyncDirection.TwoWay == syncDirection) {
        List(
          syncInternal(source, target, targetInfos.size, sourceInfos.diff(targetInfos)),
          syncInternal(target, source, sourceInfos.size, targetInfos.diff(sourceInfos))
        )
      }
      else {
        List(MongoSyncResult(collectionName))
      }
    }
    catch {
      case e: Exception => {
        logger.error(e.getMessage, e)
        List(MongoSyncResult(collectionName, exception = Some(e)))
      }
    }

  private def syncInternal(
      left: DatabaseProvider,
      right: DatabaseProvider,
      countBefore: Int,
      diff: Seq[Document]
  ): MongoSyncResult = {
    val start    = System.currentTimeMillis()
    val syncDate = new Date()
    if (diff.nonEmpty) {
      val idSet: Set[ObjectId]           = diff.map(doc => doc.getObjectId(idColumnName)).toSet
      val documentsToSync: Seq[Document] = left.dao(collectionName).find(valueFilter(idColumnName, idSet)).results()
      right.dao(collectionName).bulkWriteMany(documentsToSync).result()
      val update = combine(
        set(MongoSyncOperation.SyncColumnLastSync, syncDate),
        set(MongoSyncOperation.SyncColumnLastUpdate, syncDate)
      )
      left.dao(collectionName).updateMany(Map(), update).result()
      right.dao(collectionName).updateMany(Map(), update).result()
    }
    val countAfter: Int = right.dao(collectionName).count().result().toInt
    MongoSyncResult(
      collectionName,
      syncDate,
      true,
      diff.size,
      countBefore,
      countAfter,
      (System.currentTimeMillis() - start)
    )
  }
}

object MongoSyncOperation extends ConfigHelper {
  val MaxWaitDefault = 600
  val MaxWait: Int   = intConfig(configPath = "com.sfxcode.nosql.mongo.sync", key = "maxWait", default = MaxWaitDefault)

  val SyncColumnLastSync: String =
    stringConfig(configPath = "com.sfxcode.nosql.mongo.sync", key = "syncColumnLastSync", default = "_lastSync").get
  val SyncColumnLastUpdate: String =
    stringConfig(configPath = "com.sfxcode.nosql.mongo.sync", key = "syncColumnLastUpdate", default = "_lastUpdate").get

  val WriteSyncLogOnMaster = booleanConfig(configPath = "com.sfxcode.nosql.mongo.sync", key = "writeSyncLogOnMaster")
  val SyncLogTableName: String =
    stringConfig(configPath = "com.sfxcode.nosql.mongo.sync", key = "syncLogTableName", default = "mongo-sync-log").get
}

//case class MongoSyncInfo(id: Any = new ObjectId(), syncDate: Date = new Date(), updateDate: Date = new Date())

case class MongoSyncResult(
    collectionName: String,
    syncDate: Date = new Date(),
    acknowleged: Boolean = false,
    synced: Int = -1,
    countBefore: Int = -1,
    countAfter: Int = -1,
    syncTime: Long = -1,
    exception: Option[Exception] = None
)
