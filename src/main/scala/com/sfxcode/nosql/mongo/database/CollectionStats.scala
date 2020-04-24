package com.sfxcode.nosql.mongo.database

import java.util.Date

import com.sfxcode.nosql.mongo._
import org.mongodb.scala.bson.Document

case class CollectionStats(
    ns: String,
    collectionType: String,
    scaleFactor: Int,
    size: Double,
    count: Int,
    storageSize: Int,
    avgObjSize: Int,
    nindexes: Int,
    indexSizes: Map[String, Int],
    totalIndexSize: Int,
    indexDetails: Map[String, Map[String, Any]],
    ok: Int,
    fetched: Date = new Date()
)

object CollectionStats {
  def apply(document: Document): CollectionStats = {
    val map = document.asPlainMap
    CollectionStats(
      map.getOrElse("ns", "-").toString,
      map.getOrElse("type", "Standard").toString,
      map.getOrElse("scaleFactor", 0).asInstanceOf[Int],
      map.getOrElse("size", 0).asInstanceOf[Int],
      map.getOrElse("count", 0).asInstanceOf[Int],
      map.getOrElse("storageSize", 0).asInstanceOf[Int],
      map.getOrElse("avgObjSize", 0).asInstanceOf[Int],
      map.getOrElse("nindexes", 0).asInstanceOf[Int],
      map.getOrElse("indexSizes", Map).asInstanceOf[Map[String, Int]],
      map.getOrElse("totalIndexSize", 0).asInstanceOf[Int],
      map.getOrElse("indexDetails", Map()).asInstanceOf[Map[String, Map[String, Any]]],
      map.getOrElse("ok", 0).asInstanceOf[Double].toInt
    )
  }
}
