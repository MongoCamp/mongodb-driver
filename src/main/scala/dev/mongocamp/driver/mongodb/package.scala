package dev.mongocamp.driver

import dev.mongocamp.driver.mongodb.bson.convert.JsonDateTimeConverter
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.json.CirceSchema
import dev.mongocamp.driver.mongodb.operation.ObservableIncludes
import io.circe.generic.AutoDerivation
import java.util.Date
import org.bson.json.JsonMode
import org.bson.json.JsonWriterSettings
import org.mongodb.scala.Document
import scala.language.implicitConversions

package object mongodb extends ObservableIncludes with DocumentIncludes with CirceSchema with AutoDerivation {

  implicit class DocumentExtensions[A <: Document](val document: A) extends AnyVal {

    def asPlainMap: Map[String, Any] = BsonConverter.asMap(document)

    def asPlainJson: String = {
      val builder = JsonWriterSettings.builder
        .dateTimeConverter(new JsonDateTimeConverter())
        .outputMode(JsonMode.RELAXED)
      document.toJson(builder.build())
    }

    def getValueOption(key: String): Option[Any] = BsonConverter.documentValueOption(document, key)

    def getValue(key: String): Any = getValueOption(key).orNull

    def getStringValue(key: String): String = {
      getValue(key) match {
        case n: Any => n.toString
        case _      => ""
      }
    }

    def getLongValue(key: String): Long = {
      val value = getValue(key)
      value match {
        case n: Number => n.longValue()
        case s: String => s.toLong
        case _         => 0
      }
    }

    def getIntValue(key: String): Int = getLongValue(key).intValue()

    def getDoubleValue(key: String): Double = {
      getValue(key) match {
        case n: Number => n.doubleValue()
        case s: String => s.toDouble
        case _         => 0
      }
    }

    def getDateValue(key: String): Date = {
      val value = getValue(key)
      value match {
        case date: Date => date
        case _          => null
      }
    }

    def getFloatValue(key: String): Float = getDoubleValue(key).floatValue()

  }
}
