package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.bson.BsonConverter
import org.json4s.Formats
import org.json4s.native.Serialization.read
import org.mongodb.scala.Document

import scala.reflect.Manifest

/**
 * Created by tom on 20.01.17.
 */
object Converter {

  def toDocument(value: Any): Document = BsonConverter.toBson(value).asDocument()

  def fromDocument[A <: Any](document: Document)(implicit formats: Formats, mf: Manifest[A]): A = {
    val json = document.toJson()
    read[A](json)
  }

}
