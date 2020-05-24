package com.sfxcode.nosql.mongo.sync
import com.sfxcode.nosql.mongo._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document

object TestSync {

  val TestCollectionSourceTargetName = "sync-test-source-target"

  val mongoSyncer: MongoSyncer =
    MongoSyncer.fromPath(sourceConfigPath = "unit.test.mongo", targetConfigPath = "unit.test.mongo.local")

  mongoSyncer.addOperation(MongoSyncOperation(TestCollectionSourceTargetName, SyncDirection.SourceToTarget))
  reset()

  def insertIntoSource(count: Int = 1): Unit =
    (1 to count).foreach { _ =>
      mongoSyncer.source
        .collection(TestCollectionSourceTargetName)
        .insertOne(Document("_id" -> new ObjectId(), "string" -> "Hallo", "long" -> 1))
        .result()
    }

  def insertIntoTarget(count: Int = 1): Unit =
    (1 to count).foreach { _ =>
      mongoSyncer.target
        .collection(TestCollectionSourceTargetName)
        .insertOne(Document("_id" -> new ObjectId(), "string" -> "Hallo", "long" -> 1))
        .result()
    }

  def sourceCount: Long = mongoSyncer.source.dao(TestCollectionSourceTargetName).count().result()
  def targetCount: Long = mongoSyncer.target.dao(TestCollectionSourceTargetName).count().result()

  def reset(): Unit = {
    mongoSyncer.source.collection(TestCollectionSourceTargetName).drop().result()
    mongoSyncer.target.collection(TestCollectionSourceTargetName).drop().result()
  }
}
