package dev.mongocamp.driver.mongodb.schema

import better.files.Resource
import io.circe.{ Decoder, HCursor }
import io.circe.jawn.decode
import io.circe.syntax._
import io.circe.generic.auto._
import org.bson.Document

import scala.collection.mutable

class JsonConverter extends CirceSchema {

  def toJson(s: Any): String = {
    s.asJson.noSpaces
  }

  def readJsonMap(fileContent: String): Map[String, Any] = {
    val decoded = decode[Map[String, Any]](fileContent)
    decoded.getOrElse(Map())
  }

  def readJsonMapFromFile(fileName: String): Map[String, Any] = {
    val fileContent = Resource.asString(fileName).getOrElse("{}")
    readJsonMap(fileContent)
  }

  def toObject[A](jsonString: String)(implicit decoder: Decoder[A]): A = {
    decode[A](jsonString).getOrElse(null.asInstanceOf[A])
  }

}

object JsonConverter extends CirceSchema {}
