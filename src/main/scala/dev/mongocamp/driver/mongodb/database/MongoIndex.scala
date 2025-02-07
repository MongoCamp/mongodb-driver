package dev.mongocamp.driver.mongodb.database

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.operation.ObservableIncludes
import org.mongodb.scala.ListIndexesObservable
import org.mongodb.scala.model.IndexOptions

import java.util.Date

case class MongoIndex(
    name: String,
    fields: List[String],
    unique: Boolean,
    version: Int,
    namespace: String,
    keys: Map[String, Any],
    weights: Map[String, Any],
    expire: Boolean,
    expireAfterSeconds: Long,
    text: Boolean,
    fetched: Date,
    map: Map[String, Any]
)

object MongoIndex extends ObservableIncludes with LazyLogging {

  def indexOptionsWithName(name: Option[String]): IndexOptions =
    if (name.isDefined)
      IndexOptions().name(name.get)
    else
      IndexOptions()

  def hasIndexForFieldWithName(
      listIndexesObservable: ListIndexesObservable[Map[String, Any]],
      fieldName: String,
      maxWait: Int = DefaultMaxWait
  ): Boolean =
    convertIndexDocumentsToMongoIndexList(listIndexesObservable, maxWait).exists(index => index.fields.contains(fieldName))

  def convertIndexDocumentsToMongoIndexList(
      listIndexesObservable: ListIndexesObservable[Map[String, Any]],
      maxWait: Int = DefaultMaxWait
  ): List[MongoIndex] = {
    var result = List[MongoIndex]()
    try result = listIndexesObservable
      .resultList(maxWait)
      .map(indexOptions =>
        MongoIndex(
          indexOptions("name").toString,
          if (indexOptions.contains("textIndexVersion"))
            indexOptions.getOrElse("weights", Map()).asInstanceOf[Map[String, _]].keys.toList
          else
            indexOptions.getOrElse("key", Map).asInstanceOf[Map[String, _]].keys.toList,
          indexOptions.getOrElse("unique", indexOptions("name").toString.equalsIgnoreCase("_id_")).asInstanceOf[Boolean],
          indexOptions.getOrElse("v", -1).asInstanceOf[Int],
          indexOptions.getOrElse("ns", "").toString,
          indexOptions.getOrElse("key", Map).asInstanceOf[Map[String, _]],
          indexOptions.getOrElse("weights", Map()).asInstanceOf[Map[String, _]],
          indexOptions.contains("expireAfterSeconds"),
          indexOptions.getOrElse("expireAfterSeconds", -1).toString.toLong,
          indexOptions.contains("textIndexVersion"),
          new Date(),
          indexOptions
        )
      )
    catch {
      case e: Exception => logger.error(e.getMessage, e)
    }

    result
  }

}
