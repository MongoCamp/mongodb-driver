package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.bson.BsonConverter
import org.mongodb.scala.bson.BsonValue
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.BsonField

object Field {

  def firstField(fieldName: String): BsonField = first(fieldName, "$" + fieldName)

  def minField(fieldName: String): BsonField = min(fieldName, "$" + fieldName)

  def lastField(fieldName: String): BsonField = last(fieldName, "$" + fieldName)

  def sumField(fieldName: String): BsonField = sum(fieldName, "$" + fieldName)

  def avgField(fieldName: String): BsonField = avg(fieldName, "$" + fieldName)

  def groupField(fieldName: String): BsonValue = groupFields(List(fieldName))

  def firstFields(fieldnames: Iterable[String]): Set[BsonField] = {
    fieldnames.map(fieldname => firstField(fieldname)).toSet
  }

  def minFields(fieldnames: Iterable[String]): Set[BsonField] = {
    fieldnames.map(fieldname => minField(fieldname)).toSet
  }

  def lastFields(fieldnames: Iterable[String]): Set[BsonField] = {
    fieldnames.map(fieldname => lastField(fieldname)).toSet
  }

  def sumFields(fieldnames: Iterable[String]): Set[BsonField] = {
    fieldnames.map(fieldname => sumField(fieldname)).toSet
  }

  def avgFields(fieldnames: Iterable[String]): Set[BsonField] = {
    fieldnames.map(fieldname => avgField(fieldname)).toSet
  }

  def groupFields(fieldnames: Iterable[String]): BsonValue = {
    val list = fieldnames.map(name => {
      if (name.startsWith("$"))
        name
      else "$" + name
    }).toList
    BsonConverter.toBson(Map("_id" -> list))
  }

}
