package dev.mongocamp.driver.mongodb.json

import better.files.Resource
import com.typesafe.scalalogging.LazyLogging
import io.circe.jawn.decode
import io.circe.syntax._
import io.circe.Decoder
import io.circe.Encoder

class JsonConverter(dropNullValues: Boolean = false) extends CirceSchema with LazyLogging {

  def toJson[A <: Any](s: A)(implicit encoder: Encoder[A]): String = {
    if (dropNullValues) {
      s.asJson.deepDropNullValues.noSpaces
    }
    else {
      s.asJson.noSpaces
    }
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
    val decodeResponse = decode[A](jsonString)
    if (decodeResponse.isLeft) {
      logger.warn(s"Error while decoding json: ${decodeResponse.left.get}")
    }
    decodeResponse.getOrElse(null.asInstanceOf[A])
  }

}
