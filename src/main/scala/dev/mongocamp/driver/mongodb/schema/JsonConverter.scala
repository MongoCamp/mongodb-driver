package dev.mongocamp.driver.mongodb.schema

import better.files.Resource
import io.circe.jawn.decode
import io.circe.syntax._
import io.circe.generic.auto._
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

}
