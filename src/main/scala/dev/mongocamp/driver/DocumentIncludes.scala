package dev.mongocamp.driver

import dev.mongocamp.driver.mongodb.Converter
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import org.bson.types.ObjectId
import org.mongodb.scala.Document
import org.mongodb.scala.bson.conversions.Bson

import scala.jdk.CollectionConverters._
import scala.language.implicitConversions
trait DocumentIncludes {
  implicit def mapToBson(value: Map[_, _]): Bson = Converter.toDocument(value)

  implicit def documentFromJavaMap(map: java.util.Map[String, Any]): Document =
    documentFromScalaMap(map.asScala.toMap)

  implicit def documentFromMutableMap(map: collection.mutable.Map[String, Any]): Document =
    documentFromScalaMap(map.toMap)

  implicit def documentFromScalaMap(map: Map[String, Any]): Document = {
    var result = Document()
    map.keys.foreach { key =>
      val v = map.getOrElse(key, null)
      result.+=(key -> BsonConverter.toBson(v))
    }
    result
  }

  implicit def documentFromDocument(doc: org.bson.Document): Document = {
    var result = Document()
    doc
      .keySet()
      .asScala
      .foreach { key =>
        val v = doc.get(key)
        result.+=(key -> BsonConverter.toBson(v))
      }
    result
  }

  implicit def mapFromDocument(document: Document): Map[String, Any] =
    BsonConverter.asMap(document)

  implicit def mapListFromDocuments(documents: List[Document]): List[Map[String, Any]] =
    BsonConverter.asMapList(documents)

  // ObjectId
  implicit def stringToObjectId(str: String): ObjectId = new ObjectId(str)

  implicit def documentToObjectId(doc: Document): ObjectId =
    doc.getObjectId(DatabaseProvider.ObjectIdKey)
}
