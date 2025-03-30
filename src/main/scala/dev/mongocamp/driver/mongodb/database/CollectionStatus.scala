package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb._
import java.util.Date
import org.mongodb.scala.bson.Document

case class CollectionStatus(
  ns: String,
  collectionType: String,
  scaleFactor: Int,
  size: Double,
  count: Int,
  storageSize: Double,
  avgObjSize: Int,
  nindexes: Int,
  indexSizes: Map[String, Int],
  totalIndexSize: Int,
  ok: Int,
  fetched: Date,
  map: Map[String, Any]
)

object CollectionStatus {
  def apply(document: Document): CollectionStatus = {
    val map = document.asPlainMap
    CollectionStatus(
      map.getOrElse("ns", "-").toString,
      map.getOrElse("type", "Standard").toString,
      map.getOrElse("scaleFactor", 0).asInstanceOf[Int],
      doubleValue(map, "size"),
      intValue(map, "count"),
      doubleValue(map, "storageSize"),
      intValue(map, "avgObjSize"),
      intValue(map, "nindexes"),
      map.getOrElse("indexSizes", Map()).asInstanceOf[Map[String, Int]],
      intValue(map, "totalIndexSize"),
      intValue(map, "ok"),
      new Date(),
      map
    )
  }

  def intValue(map: Map[String, Any], key: String): Int =
    map.getOrElse(key, 0) match {
      case i: Int    => i
      case l: Long   => l.intValue()
      case d: Double => d.intValue()
      case _         => 0
    }

  def doubleValue(map: Map[String, Any], key: String): Double =
    map.getOrElse(key, 0) match {
      case i: Int    => i.doubleValue()
      case l: Long   => l.doubleValue()
      case d: Double => d
      case _         => 0
    }
}
