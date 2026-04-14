package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb._
import java.util.Date
import org.mongodb.scala.bson.Document
import org.mongodb.scala.documentToUntypedDocument

case class CollectionInfo(name: String, collectionType: String, fetched: Date, map: Map[String, Any]) {

  def isCapped: Boolean = {
    map.get("options").exists {
      case opts: Map[_, _] => opts.asInstanceOf[Map[String, Any]].get("capped").contains(true)
      case _               => false
    }
  }

  def isTimeSeries: Boolean = {
    collectionType == "timeseries"
  }

}

object CollectionInfo {

  def apply(document: Document): CollectionInfo = {
    val name           = document.getString("name")
    val collectionType = document.getString("type")
    CollectionInfo(name, collectionType, new Date(), document.asPlainMap)
  }
}
