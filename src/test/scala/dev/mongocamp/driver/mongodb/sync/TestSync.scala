package dev.mongocamp.driver.mongodb.sync
import dev.mongocamp.driver.mongodb._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document

object TestSync {

  val TestCollectionSourceTargetName = "sync-test-source-target"
  val TestCollectionTwoWayName       = "sync-test-two-way"

  val mongoSyncer: MongoSyncer =
    MongoSyncer.fromPath(sourceConfigPath = "unit.test.mongo", targetConfigPath = "unit.test.mongo.local")

  mongoSyncer.addOperation(MongoSyncOperation(TestCollectionSourceTargetName, SyncDirection.SourceToTarget))
  mongoSyncer.addOperation(MongoSyncOperation(TestCollectionTwoWayName, SyncDirection.TwoWay))
  reset()

  def insertIntoSource(count: Int = 1, collectionName: String): Unit =
    (1 to count).foreach { _ =>
      mongoSyncer.source
        .collection(collectionName)
        .insertOne(Document("_id" -> new ObjectId(), "string" -> "Hallo", "long" -> 1))
        .result()
    }

  def insertIntoTarget(count: Int = 1, collectionName: String): Unit =
    (1 to count).foreach { _ =>
      mongoSyncer.target
        .collection(collectionName)
        .insertOne(Document("_id" -> new ObjectId(), "string" -> "Hallo", "long" -> 1))
        .result()
    }

  def sourceCount(collectionName: String): Long =
    mongoSyncer.source.dao(collectionName).count().result()
  def targetCount(collectionName: String): Long =
    mongoSyncer.target.dao(collectionName).count().result()

  def reset(): Unit = {
    mongoSyncer.source.collection(TestCollectionSourceTargetName).drop().result()
    mongoSyncer.target.collection(TestCollectionSourceTargetName).drop().result()

    mongoSyncer.source.collection(TestCollectionTwoWayName).drop().result()
    mongoSyncer.target.collection(TestCollectionTwoWayName).drop().result()
  }
}
