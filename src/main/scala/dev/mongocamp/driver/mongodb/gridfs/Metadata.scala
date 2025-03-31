package dev.mongocamp.driver.mongodb.gridfs

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.MongoDAO
import org.bson.types.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.Document
import org.mongodb.scala.Observable

abstract class Metadata(provider: DatabaseProvider, bucketName: String) extends Crud {

  def filesCollectionName: String = "%s.%s".format(bucketName, "files")

  def chunksCollectionName: String = "%s.%s".format(bucketName, "chunks")

  def updateMetadata(oid: ObjectId, value: Any): Observable[UpdateResult] = {
    val doc: BsonValue = BsonConverter.toBson(value)
    val result         = Files.updateOne(equal(DatabaseProvider.ObjectIdKey, oid), set("metadata", doc))
    result
  }

  def updateMetadataElements(filter: Bson, updates: Bson*): Observable[UpdateResult] = {
    val result = Files.updateMany(filter, combine(updates: _*))
    result
  }

  def updateMetadataElements(filter: Bson, elements: Map[String, Any]): Observable[UpdateResult] = {
    val list = elements
      .map(
        entry => set("metadata.%s".format(entry._1), BsonConverter.toBson(entry._2))
      )
      .toList
    updateMetadataElements(filter, list: _*)
  }

  def updateMetadataElements(oid: ObjectId, elements: Map[String, Any]): Observable[UpdateResult] =
    updateMetadataElements(equal(DatabaseProvider.ObjectIdKey, oid), elements)

  def updateMetadataElement(oid: ObjectId, key: String, value: Any): Observable[UpdateResult] =
    updateMetadataElements(equal(DatabaseProvider.ObjectIdKey, oid), Map(key -> value))

  def updateMetadataElement(filter: Bson, key: String, value: Any): Observable[UpdateResult] =
    updateMetadataElements(filter, Map(key -> value))

  object Files  extends MongoDAO[Document](provider, filesCollectionName)
  object Chunks extends MongoDAO[Document](provider, chunksCollectionName)

}
