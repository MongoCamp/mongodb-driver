package com.sfxcode.nosql.mongo.database

import java.util.Date

import org.mongodb.scala.bson.Document

case class CollectionInfo(
    name: String,
    collectionType: String,
    fetched: Date,
    document: Document
)

object CollectionInfo {

  def apply(document: Document): CollectionInfo = {
    val name           = document.getString("name")
    val collectionType = document.getString("type")
    CollectionInfo(name, collectionType, new Date(), document)
  }
}
