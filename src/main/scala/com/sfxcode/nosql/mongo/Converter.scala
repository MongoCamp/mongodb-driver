package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.bson.BsonConverter
import org.mongodb.scala.Document

object Converter {

  def toDocument(value: Any): Document =
    BsonConverter.toBson(value).asDocument()

}
