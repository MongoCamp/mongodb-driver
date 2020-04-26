package com.sfxcode.nosql.mongo.database

import java.util.Date

import com.sfxcode.nosql.mongo._
import org.mongodb.scala.bson.Document

case class CollectionStatus(
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
    ok: Int,
    fetched: Date,
    document: Document
)

object CollectionStatus {
  def apply(document: Document): CollectionStatus = {
    val map = document.asPlainMap
    CollectionStatus(
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
      map.getOrElse("ok", 0).asInstanceOf[Double].toInt,
      new Date(),
      document
    )
  }
}
