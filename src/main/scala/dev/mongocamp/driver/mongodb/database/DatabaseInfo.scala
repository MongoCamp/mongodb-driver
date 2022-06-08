package dev.mongocamp.driver.mongodb.database

import java.util.Date
import dev.mongocamp.driver.mongodb._

import org.mongodb.scala.bson.Document

case class DatabaseInfo(name: String, sizeOnDisk: Double, empty: Boolean, fetched: Date, map: Map[String, Any])

object DatabaseInfo {

  def apply(document: Document): DatabaseInfo = {
    val name       = document.getString("name")
    val sizeOnDisk = document.getValue("sizeOnDisk").asInstanceOf[Number].doubleValue()
    val empty      = document.getBoolean("empty")
    DatabaseInfo(name, sizeOnDisk, empty, new Date(), document.asPlainMap)

  }
}
