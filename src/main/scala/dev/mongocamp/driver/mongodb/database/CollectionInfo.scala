package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb._
import org.mongodb.scala.bson.Document
import org.mongodb.scala.documentToUntypedDocument

import java.util.Date

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
