package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.bson.BsonConverter
import org.mongodb.scala.Document

/**
 * Created by tom on 20.01.17.
 */
object Converter {

  def toDocument(value: Any): Document =
    BsonConverter.toBson(value).asDocument()

}
