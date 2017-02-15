package com.sfxcode.nosql.mongo

import com.typesafe.scalalogging.LazyLogging
import org.bson.conversions.Bson
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters.{ equal, in, not }

object Filter extends LazyLogging {
  val DefaultBson: Bson = BsonDocument(Document())

  def valueFilter(key: String, value: Any): Bson = {

    value match {
      case list: List[_] =>
        in(key, list: _*)
      case set: Set[_] =>
        in(key, set.toSeq: _*)
      case id: Any =>
        equal(key, value)
      case _ => DefaultBson
    }
  }

  def nullFilter(fieldName: String): Bson = equal(fieldName, null)

  def notNullFilter(fieldName: String): Bson = not(nullFilter(fieldName))
}
