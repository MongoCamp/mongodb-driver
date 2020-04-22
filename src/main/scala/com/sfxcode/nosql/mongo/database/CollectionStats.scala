package com.sfxcode.nosql.mongo.database

import java.util.Date

import com.sfxcode.nosql.mongo._
import org.mongodb.scala.bson.Document

case class CollectionStats(
  ns: String,
  collectionType: String,
  size: Double,
  count: Long,
  storageSize: Double,
  avgObjSize: Double,
  nindexes: Double,
  indexSizes: Map[String, Int],
  totalIndexSize: Double,
  indexDetails: Map[String, Map[String, Any]],
  scaleFactor: Double,
  ok: Long,
  fetched: Date = new Date())

object CollectionStats {
  def apply(document: Document): CollectionStats = {
    val map = document.asPlainMap
    CollectionStats(
      map("ns").toString,
      map.getOrElse("type", "Standard").toString,
      map("size").asInstanceOf[Int],
      map("count").asInstanceOf[Int],
      map("storageSize").asInstanceOf[Int],
      map("avgObjSize").asInstanceOf[Int],
      map("nindexes").asInstanceOf[Int],
      map("indexSizes").asInstanceOf[Map[String, Int]],
      map("totalIndexSize").asInstanceOf[Int],
      map("indexDetails").asInstanceOf[Map[String, Map[String, Any]]],
      map("scaleFactor").asInstanceOf[Int],
      map("ok").asInstanceOf[Double].toInt)
  }
}
