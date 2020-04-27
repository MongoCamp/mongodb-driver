package com.sfxcode.nosql.mongo.database

import java.util.Date
import com.sfxcode.nosql.mongo._

import org.mongodb.scala.bson.Document

case class DatabaseInfo(name: String, sizeOnDisk: Double, empty: Boolean, fetched: Date, map: Map[String, Any])

object DatabaseInfo {

  def apply(document: Document): DatabaseInfo = {
    val name       = document.getString("name")
    val sizeOnDisk = document.getDouble("sizeOnDisk")
    val empty      = document.getBoolean("empty")
    DatabaseInfo(name, sizeOnDisk, empty, new Date(), document.asPlainMap)

  }
}
