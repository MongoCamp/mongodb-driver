package com.sfxcode.nosql.mongo.database

import java.util.Date

import org.mongodb.scala.bson.Document
import com.sfxcode.nosql.mongo._

case class CollectionInfo(
    name: String,
    collectionType: String,
    fetched: Date,
    map: Map[String, Any]
)

object CollectionInfo {

  def apply(document: Document): CollectionInfo = {
    val name           = document.getString("name")
    val collectionType = document.getString("type")
    CollectionInfo(name, collectionType, new Date(), document.asPlainMap)
  }
}
