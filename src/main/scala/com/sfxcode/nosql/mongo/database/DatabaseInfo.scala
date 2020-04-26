package com.sfxcode.nosql.mongo.database

import java.util.Date

import org.mongodb.scala.bson.Document

case class DatabaseInfo(name: String, sizeOnDisk: Double, empty: Boolean, fetched: Date, document: Document)

object DatabaseInfo {

  def apply(document: Document): DatabaseInfo = {
    val name       = document.getString("name")
    val sizeOnDisk = document.getDouble("sizeOnDisk")
    val empty      = document.getBoolean("empty")
    DatabaseInfo(name, sizeOnDisk, empty, new Date(), document)

  }
}
