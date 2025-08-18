package dev.mongocamp.driver.mongodb.json

import better.files.Resource
import com.typesafe.scalalogging.LazyLogging
import io.circe.jawn.decode
import io.circe.syntax._
import io.circe.Decoder
import io.circe.Encoder

class JsonConverter(dropNullValues: Boolean = false, defaultShouldLogAsError: Boolean = false) extends CirceSchema with LazyLogging {

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

  def toObjectRaw[A](jsonString: String)(implicit decoder: Decoder[A]): Either[io.circe.Error, A] = {
    val decodeResponse = decode[A](jsonString)
    decodeResponse
  }

  def toObject[A](jsonString: String)(implicit decoder: Decoder[A]): A = {
    toObjectOption[A](jsonString, defaultShouldLogAsError).getOrElse(null.asInstanceOf[A])
  }

  def toObject[A](jsonString: String, shouldLogError: Boolean = defaultShouldLogAsError)(implicit decoder: Decoder[A]): A = {
    toObjectOption[A](jsonString, shouldLogError).getOrElse(null.asInstanceOf[A])
  }

  def toObjectOption[A](jsonString: String, shouldLogError: Boolean = defaultShouldLogAsError)(implicit decoder: Decoder[A]): Option[A] = {
    val decodeResponse = toObjectRaw[A](jsonString)
    if (decodeResponse.isLeft) {
      if (shouldLogError) {
        logger.error(s"Error while decoding json: ${decodeResponse.left}")
      }
      else {
        logger.debug(s"Error while decoding json: ${decodeResponse.left}")
      }
    }
    decodeResponse.toOption
  }

}
