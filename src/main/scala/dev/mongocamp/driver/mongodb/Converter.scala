package dev.mongocamp.driver.mongodb

import dev.mongocamp.driver.mongodb.bson.BsonConverter
import org.mongodb.scala.Document

object Converter {

  def toDocument(value: Any): Document =
    BsonConverter.toBson(value).asDocument()

}
