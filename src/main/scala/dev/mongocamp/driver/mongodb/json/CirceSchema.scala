package dev.mongocamp.driver.mongodb.json

import dev.mongocamp.driver.mongodb.bson.BsonConverter
import io.circe.Decoder
import io.circe.Decoder.Result
import io.circe.Encoder
import io.circe.HCursor
import io.circe.Json
import java.util.Date
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.jdk.CollectionConverters.*

/** Frozen binary compatibility layer for libraries compiled against mongodb-driver 3.0.x (e.g. the generated rest clients).
  *
  * Those classes contain the fields and setters of the vals declared here and call `CirceSchema.$init$` and the static
  * `CirceSchema.<method>$` forwarders. Do not add, remove, rename or retype any member of this trait. Use [[CirceCodecs]].
  */
@deprecated("use CirceCodecs", "3.3.4")
trait CirceSchema extends CirceProductSchema {

  implicit lazy val DocumentOneFormat: Encoder[org.mongodb.scala.Document] & io.circe.Decoder[org.mongodb.scala.Document] =
    new io.circe.Encoder[org.mongodb.scala.Document] with io.circe.Decoder[org.mongodb.scala.Document] {
      override def apply(a: org.mongodb.scala.Document): Json = {
        encodeMapStringAny(BsonConverter.asMap(a))
      }

      override def apply(c: HCursor): Result[org.mongodb.scala.Document] = {
        Decoder.decodeString
          .map(
            s => {
              val document = new org.mongodb.scala.Document(org.mongodb.scala.bson.BsonDocument(s))
              document
            }
          )
          .apply(c)
      }
    }

  implicit lazy val DocumentTowFormat: Encoder[org.bson.Document] & io.circe.Decoder[org.bson.Document] = new io.circe.Encoder[org.bson.Document]
    with io.circe.Decoder[org.bson.Document] {
    override def apply(a: org.bson.Document): Json = {
      val map = a
        .keySet()
        .asScala
        .map(
          key => {
            val value = a.get(key)
            (key, encodeAnyToJson(value))
          }
        )
        .toMap
      encodeMapStringAny(map)
    }

    override def apply(c: HCursor): Result[org.bson.Document] = {
      Decoder
        .decodeMap[String, Any]
        .map(
          m => {
            val document = new org.bson.Document(m.asJava)
            document
          }
        )
        .apply(c)
    }
  }

  implicit val DateFormat: Encoder[Date] & io.circe.Decoder[Date] = new io.circe.Encoder[Date] with io.circe.Decoder[Date] {
    override def apply(d: Date): Json = {
      Option(d)
        .map(
          date => Encoder.encodeString.apply(date.toInstant.toString)
        )
        .getOrElse(Json.Null)
    }

    override def apply(c: HCursor): Result[Date] = {
      Decoder.decodeString
        .map(
          s => new DateTime(s).toDate
        )
        .apply(c)
    }
  }

  implicit val DateTimeFormat: Encoder[DateTime] & io.circe.Decoder[DateTime] = new io.circe.Encoder[DateTime] with io.circe.Decoder[DateTime] {
    override def apply(d: DateTime): Json = {
      Option(d)
        .map(
          date => Encoder.encodeString.apply(date.toInstant.toString)
        )
        .getOrElse(Json.Null)
    }

    override def apply(c: HCursor): Result[DateTime] = {
      Decoder.decodeString
        .map(
          s => new DateTime(s)
        )
        .apply(c)
    }
  }

  implicit val ObjectIdFormat: Encoder[ObjectId] & io.circe.Decoder[ObjectId] = new io.circe.Encoder[ObjectId] with io.circe.Decoder[ObjectId] {
    override def apply(o: ObjectId): Json = {
      Option(o)
        .map(
          o => Encoder.encodeString.apply(o.toHexString)
        )
        .getOrElse(Json.Null)
    }

    override def apply(c: HCursor): Result[ObjectId] = {
      Decoder.decodeString
        .map(
          s => new ObjectId(s)
        )
        .apply(c)
    }
  }

  implicit val MapStringAnyFormat: Encoder[Map[String, Any]] & io.circe.Decoder[Map[String, Any]] = new io.circe.Encoder[Map[String, Any]]
    with io.circe.Decoder[Map[String, Any]] {
    override def apply(a: Map[String, Any]): Json = {
      encodeMapStringAny(a)
    }

    override def apply(c: HCursor): Result[Map[String, Any]] = {
      Decoder.decodeMap[String, Any].apply(c)
    }
  }

  implicit val AnyFormat: Encoder[Any] & io.circe.Decoder[Any] = new io.circe.Encoder[Any] with io.circe.Decoder[Any] {

    override def apply(a: Any): Json = {
      encodeAnyToJson(a)
    }

    override def apply(c: HCursor): Result[Any] = {
      Decoder.decodeJson
        .map(
          a => decodeFromJson(a)
        )
        .apply(c)
    }

  }

  def encodeMapStringAny(a: Map[String, Any]): Json = CirceConverter.encodeMapStringAny(a)

  def decodeFromJson(json: Json): Any = CirceConverter.decodeFromJson(json)

  def encodeAnyToJson(a: Any, depth: Int = 0): Json = CirceConverter.encodeAnyToJson(a, depth)

}
