package dev.mongocamp.driver.mongodb

import org.bson.conversions.Bson
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._

import java.util.Date

object Filter extends Filter

trait Filter {
  val DefaultBson: Bson = BsonDocument(Document())

  def valueFilter(key: String, value: Any): Bson =
    value match {
      case list: List[_] =>
        in(key, list: _*)
      case set: Set[_] =>
        in(key, set.toSeq: _*)
      case _: Any =>
        equal(key, value)
      case _ => DefaultBson
    }

  def fieldComparisonFilter(firstFieldName: String, secondFieldName: String, operator: String): Bson =
    where("this.%s %s this.%s".format(firstFieldName, operator, secondFieldName))

  def nullFilter(fieldName: String): Bson = equal(fieldName, value = null)

  def notNullFilter(fieldName: String): Bson = not(nullFilter(fieldName))

  def dateInRangeFilter(dateFieldKey: String, dateFrom: Date = null, dateUntil: Date = null): Bson =
    if (dateFrom != null && dateUntil != null)
      and(gte(dateFieldKey, dateFrom), lte(dateFieldKey, dateUntil))
    else if (dateUntil != null)
      lte(dateFieldKey, dateUntil)
    else if (dateFrom != null)
      gte(dateFieldKey, dateFrom)
    else
      Map()

}
