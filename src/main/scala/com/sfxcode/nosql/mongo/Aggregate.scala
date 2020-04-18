package com.sfxcode.nosql.mongo

import org.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.sort
import org.mongodb.scala.model.Projections.computed

object Aggregate extends Aggregate

trait Aggregate extends Field with Filter with Sort {

  def compositeProjection(resultFieldName: String, keys: List[String]): Bson =
    computed(resultFieldName,
             Map[String, Any]("$concat" -> keys.map(key => Map[String, Any]("$substr" -> List("$" + key, 0, 99999)))))

  def divideProjection(resultFieldName: String, dividendFieldName: String, divisorFieldName: String): Bson =
    computed(resultFieldName, Map[String, Any]("$divide" -> List("$" + dividendFieldName, "$" + divisorFieldName)))

  def multiplyProjection(resultFieldName: String, productFieldNames: List[String]): Bson =
    computed(resultFieldName, Map[String, Any]("$multiply" -> productFieldNames.map(fieldname => "$" + fieldname)))

  def sortStage(key: String, sortAscending: Boolean = true): Bson =
    sort(sortByKey(key, sortAscending))

}
