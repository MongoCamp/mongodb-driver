package dev.mongocamp.driver.mongodb.json

import dev.mongocamp.driver.mongodb.bson.BsonConverter
import io.circe._
import io.circe.Decoder.Result
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import org.bson.types.ObjectId
import org.joda.time.DateTime
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.util.Try

trait CirceCodecs extends CirceConverter {

  implicit lazy val DocumentOneCodec: Codec[org.mongodb.scala.Document] =
    new Codec[org.mongodb.scala.Document] {
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

  implicit lazy val DocumentTowCodec: Codec[org.bson.Document] =
    new Codec[org.bson.Document] {
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

  implicit lazy val DateCodec: Codec[Date] = new Codec[Date] {
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

  implicit lazy val DateTimeCodec: Codec[DateTime] = new Codec[DateTime] {
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

  implicit lazy val JavaLocalDateCodec: Codec[java.time.LocalDate] = {
    stringCodec[java.time.LocalDate](_.toString, s => javaZonedDateTimeFromString(s).toLocalDate)
  }

  implicit lazy val JavaLocalDateTimeCodec: Codec[java.time.LocalDateTime] = {
    stringCodec[java.time.LocalDateTime](_.toString, s => javaZonedDateTimeFromString(s).toLocalDateTime)
  }

  implicit lazy val SqlDateCodec: Codec[java.sql.Date] = {
    stringCodec[java.sql.Date](d => Instant.ofEpochMilli(d.getTime).toString, s => new java.sql.Date(new DateTime(s).getMillis))
  }

  implicit lazy val SqlTimestampCodec: Codec[java.sql.Timestamp] = {
    stringCodec[java.sql.Timestamp](_.toInstant.toString, s => new java.sql.Timestamp(new DateTime(s).getMillis))
  }

  implicit lazy val JodaInstantCodec: Codec[org.joda.time.Instant] = {
    stringCodec[org.joda.time.Instant](_.toString, s => new DateTime(s).toInstant)
  }

  implicit lazy val JodaMutableDateTimeCodec: Codec[org.joda.time.MutableDateTime] = {
    stringCodec[org.joda.time.MutableDateTime](_.toInstant.toString, s => new DateTime(s).toMutableDateTime)
  }

  implicit lazy val JodaLocalDateCodec: Codec[org.joda.time.LocalDate] = {
    stringCodec[org.joda.time.LocalDate](_.toString, s => new DateTime(s).toLocalDate)
  }

  implicit lazy val JodaLocalDateTimeCodec: Codec[org.joda.time.LocalDateTime] = {
    stringCodec[org.joda.time.LocalDateTime](_.toString, s => new DateTime(s).toLocalDateTime)
  }

  implicit lazy val JodaLocalTimeCodec: Codec[org.joda.time.LocalTime] = {
    stringCodec[org.joda.time.LocalTime](_.toString, org.joda.time.LocalTime.parse)
  }

  implicit lazy val JodaDurationCodec: Codec[org.joda.time.Duration] = {
    stringCodec[org.joda.time.Duration](
      d => Duration(d.getMillis, java.util.concurrent.TimeUnit.MILLISECONDS).toString,
      s => if (s.startsWith("P")) org.joda.time.Duration.parse(s) else new org.joda.time.Duration(Duration(s).toMillis)
    )
  }

  implicit lazy val JodaPeriodCodec: Codec[org.joda.time.Period] = {
    stringCodec[org.joda.time.Period](_.toString, org.joda.time.Period.parse)
  }

  implicit lazy val JodaDateTimeZoneCodec: Codec[org.joda.time.DateTimeZone] = {
    stringCodec[org.joda.time.DateTimeZone](_.getID, org.joda.time.DateTimeZone.forID)
  }

  implicit lazy val JodaYearMonthCodec: Codec[org.joda.time.YearMonth] = {
    stringCodec[org.joda.time.YearMonth](_.toString, org.joda.time.YearMonth.parse)
  }

  implicit lazy val JodaMonthDayCodec: Codec[org.joda.time.MonthDay] = {
    stringCodec[org.joda.time.MonthDay](_.toString, org.joda.time.MonthDay.parse)
  }

  private def javaZonedDateTimeFromString(s: String): ZonedDateTime = {
    Try(OffsetDateTime.parse(s).atZoneSameInstant(ZoneId.systemDefault()))
      .orElse(Try(java.time.LocalDateTime.parse(s).atZone(ZoneId.systemDefault())))
      .getOrElse(java.time.LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()))
  }

  private def stringCodec[A](encode: A => String, decode: String => A): Codec[A] = new Codec[A] {
    override def apply(a: A): Json = {
      Option(a)
        .map(
          value => Json.fromString(encode(value))
        )
        .getOrElse(Json.Null)
    }

    override def apply(c: HCursor): Result[A] = {
      Decoder.decodeString
        .emap(
          s => Try(decode(s)).toEither.left.map(_.getMessage)
        )
        .apply(c)
    }
  }

  implicit lazy val ObjectIdCodec: Codec[ObjectId] = new Codec[ObjectId] {
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

  implicit lazy val DurationCodec: Codec[Duration] = new Codec[Duration] {
    override def apply(d: Duration): Json = {
      Option(d)
        .map(
          duration => Json.fromString(duration.toString)
        )
        .getOrElse(Json.Null)
    }

    override def apply(c: HCursor): Result[Duration] = {
      Decoder.decodeString
        .emap(
          s => scala.util.Try(Duration(s)).toEither.left.map(_.getMessage)
        )
        .apply(c)
    }
  }

  implicit lazy val MapStringAnyCodec: Codec[Map[String, Any]] =
    new Codec[Map[String, Any]] {
      override def apply(a: Map[String, Any]): Json = {
        encodeMapStringAny(a)
      }

      override def apply(c: HCursor): Result[Map[String, Any]] = {
        Decoder.decodeMap[String, Any].apply(c)
      }
    }

  implicit lazy val AnyCodec: Codec[Any] = new Codec[Any] {
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

}
