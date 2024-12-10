package dev.mongocamp.driver.mongodb

import dev.mongocamp.driver.mongodb.bson.BsonConverter
import org.mongodb.scala.Document
import org.mongodb.scala.bsonDocumentToDocument

object Converter {

  def toDocument(value: Any): Document =
    BsonConverter.toBson(value).asDocument()

}
