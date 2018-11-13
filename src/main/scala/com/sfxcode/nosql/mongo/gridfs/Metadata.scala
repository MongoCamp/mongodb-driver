package com.sfxcode.nosql.mongo.gridfs

import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.bson.BsonConverter
import org.bson.types.ObjectId
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.{Document, MongoDatabase, Observable}

abstract class Metadata(database: MongoDatabase, bucketName: String)
    extends Crud {

  def filesCollectionName: String = "%s.%s".format(bucketName, "files")

  def updateMetadata(oid: ObjectId, value: Any): Observable[UpdateResult] = {
    val doc: BsonValue = BsonConverter.toBson(value)
    val result = Files.updateOne(equal("_id", oid), set("metadata", doc))
    result
  }

  def updateMetadataElements(filter: Bson,
                             updates: Bson*): Observable[UpdateResult] = {
    val result = Files.updateMany(filter, combine(updates: _*))
    result
  }

  def updateMetadataElements(
      filter: Bson,
      elements: Map[String, Any]): Observable[UpdateResult] = {
    val list = elements
      .map(entry =>
        set("metadata.%s".format(entry._1), BsonConverter.toBson(entry._2)))
      .toList
    updateMetadataElements(filter, list: _*)
  }

  def updateMetadataElements(
      oid: ObjectId,
      elements: Map[String, Any]): Observable[UpdateResult] = {
    updateMetadataElements(equal("_id", oid), elements)
  }

  def updateMetadataElement(oid: ObjectId,
                            key: String,
                            value: Any): Observable[UpdateResult] = {
    updateMetadataElements(equal("_id", oid), Map(key -> value))
  }

  def updateMetadataElement(filter: Bson,
                            key: String,
                            value: Any): Observable[UpdateResult] = {
    updateMetadataElements(filter, Map(key -> value))
  }

  object Files extends MongoDAO[Document](database, filesCollectionName)

}
