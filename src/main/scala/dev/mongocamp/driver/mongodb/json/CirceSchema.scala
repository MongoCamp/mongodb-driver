package dev.mongocamp.driver.mongodb.json

import dev.mongocamp.driver.mongodb.bson.BsonConverter
import io.circe._
import io.circe.Decoder.Result
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.UUID
import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.mongodb.scala.Document
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.util.Try

trait CirceSchema extends CirceProductSchema {

  implicit lazy val DocumentOneFormat: Codec[org.mongodb.scala.Document] =
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

  implicit lazy val DocumentTowFormat: Codec[org.bson.Document] =
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

  implicit val DateFormat: Codec[Date] = new Codec[Date] {
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

  implicit val DateTimeFormat: Codec[DateTime] = new Codec[DateTime] {
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

  implicit val JavaLocalDateFormat: Codec[java.time.LocalDate] =
    stringFormat[java.time.LocalDate](_.toString, s => javaZonedDateTimeFromString(s).toLocalDate)

  implicit val JavaLocalDateTimeFormat: Codec[java.time.LocalDateTime] =
    stringFormat[java.time.LocalDateTime](_.toString, s => javaZonedDateTimeFromString(s).toLocalDateTime)

  implicit val SqlDateFormat: Codec[java.sql.Date] =
    stringFormat[java.sql.Date](d => Instant.ofEpochMilli(d.getTime).toString, s => new java.sql.Date(new DateTime(s).getMillis))

  implicit val SqlTimestampFormat: Codec[java.sql.Timestamp] =
    stringFormat[java.sql.Timestamp](_.toInstant.toString, s => new java.sql.Timestamp(new DateTime(s).getMillis))

  implicit val JodaInstantFormat: Codec[org.joda.time.Instant] =
    stringFormat[org.joda.time.Instant](_.toString, s => new DateTime(s).toInstant)

  implicit val JodaMutableDateTimeFormat: Codec[org.joda.time.MutableDateTime] =
    stringFormat[org.joda.time.MutableDateTime](_.toInstant.toString, s => new DateTime(s).toMutableDateTime)

  implicit val JodaLocalDateFormat: Codec[org.joda.time.LocalDate] =
    stringFormat[org.joda.time.LocalDate](_.toString, s => new DateTime(s).toLocalDate)

  implicit val JodaLocalDateTimeFormat: Codec[org.joda.time.LocalDateTime] =
    stringFormat[org.joda.time.LocalDateTime](_.toString, s => new DateTime(s).toLocalDateTime)

  implicit val JodaLocalTimeFormat: Codec[org.joda.time.LocalTime] =
    stringFormat[org.joda.time.LocalTime](_.toString, org.joda.time.LocalTime.parse)

  implicit val JodaDurationFormat: Codec[org.joda.time.Duration] =
    stringFormat[org.joda.time.Duration](
      d => Duration(d.getMillis, java.util.concurrent.TimeUnit.MILLISECONDS).toString,
      s => if (s.startsWith("P")) org.joda.time.Duration.parse(s) else new org.joda.time.Duration(Duration(s).toMillis)
    )

  implicit val JodaPeriodFormat: Codec[org.joda.time.Period] =
    stringFormat[org.joda.time.Period](_.toString, org.joda.time.Period.parse)

  implicit val JodaDateTimeZoneFormat: Codec[org.joda.time.DateTimeZone] =
    stringFormat[org.joda.time.DateTimeZone](_.getID, org.joda.time.DateTimeZone.forID)

  implicit val JodaYearMonthFormat: Codec[org.joda.time.YearMonth] =
    stringFormat[org.joda.time.YearMonth](_.toString, org.joda.time.YearMonth.parse)

  implicit val JodaMonthDayFormat: Codec[org.joda.time.MonthDay] =
    stringFormat[org.joda.time.MonthDay](_.toString, org.joda.time.MonthDay.parse)

  private def javaZonedDateTimeFromString(s: String): ZonedDateTime = {
    Try(OffsetDateTime.parse(s).atZoneSameInstant(ZoneId.systemDefault()))
      .orElse(Try(java.time.LocalDateTime.parse(s).atZone(ZoneId.systemDefault())))
      .getOrElse(java.time.LocalDate.parse(s).atStartOfDay(ZoneId.systemDefault()))
  }

  private def stringFormat[A](encode: A => String, decode: String => A): Codec[A] = new Codec[A] {
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

  implicit val ObjectIdFormat: Codec[ObjectId] = new Codec[ObjectId] {
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

  implicit lazy val DurationFormat: Codec[Duration] = new Codec[Duration] {
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

  implicit val MapStringAnyFormat: Codec[Map[String, Any]] =
    new Codec[Map[String, Any]] {
      override def apply(a: Map[String, Any]): Json = {
        encodeMapStringAny(a)
      }

      override def apply(c: HCursor): Result[Map[String, Any]] = {
        Decoder.decodeMap[String, Any].apply(c)
      }
    }

  implicit val AnyFormat: Codec[Any] = new Codec[Any] {
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

  def encodeMapStringAny(a: Map[String, Any]): Json = {
    Json.obj(
      a.keySet
        .map(
          key => (key, encodeAnyToJson(a(key)))
        )
        .toList: _*
    )
  }

  def decodeFromJson(json: Json): Any = {
    json match {
      case a if a.isNumber =>
        val value = a.asNumber.get
        val long  = value.toLong
        if (long.isDefined) {
          long.get
        }
        else {
          value.toDouble
        }
      case a if a.isString =>
        val string = a.asString.get
        if (string.length == 24 && string.substring(10, 11).equals("T") && string.endsWith("Z")) {
          try {
            val date = new DateTime(string)
            date
          }
          catch {
            case _: Exception => string
          }
        }
        else {
          string
        }
      case a if a.isBoolean => a.asBoolean.getOrElse(false)
      case a if a.isArray =>
        a.asArray.get.toList.map(
          e => decodeFromJson(e)
        )
      case a if a.isObject =>
        a.asObject.get.toMap.map(
          e => (e._1, decodeFromJson(e._2))
        )
      case a if a.isNull => null
      case _             => null
    }
  }

  def encodeAnyToJson(a: Any, depth: Int = 0): Json = {
    a match {
      case s: String      => Json.fromString(s)
      case b: Boolean     => Json.fromBoolean(b)
      case l: Long        => Json.fromLong(l)
      case i: Int         => Json.fromInt(i)
      case bi: BigInt     => Json.fromBigInt(bi)
      case bd: BigDecimal => Json.fromBigDecimal(bd)
      case d: Double      => Json.fromDoubleOrNull(d)
      case f: Float       => Json.fromFloatOrNull(f)
      case option: Option[_] =>
        option
          .map(
            e => encodeAnyToJson(e, depth)
          )
          .getOrElse(Json.Null)
      case d: Date     => Encoder.encodeString.apply(Instant.ofEpochMilli(d.getTime).toString)
      case d: DateTime => Encoder.encodeString.apply(d.toInstant.toString)
      case d: Duration => Json.fromString(d.toString)
      case o: ObjectId => Encoder.encodeString.apply(o.toHexString)
      case u: UUID     => Json.fromString(u.toString)
      case t @ (_: java.time.temporal.TemporalAccessor | _: java.time.temporal.TemporalAmount | _: ZoneId) => Json.fromString(t.toString)
      case j @ (_: org.joda.time.ReadableInstant | _: org.joda.time.ReadablePartial | _: org.joda.time.ReadablePeriod | _: org.joda.time.ReadableDuration |
          _: org.joda.time.DateTimeZone) =>
        Json.fromString(j.toString)
      case m: Map[String, _] => encodeMapStringAny(m)
      case seq: Seq[_] =>
        Json.arr(
          seq.map(
            e => encodeAnyToJson(e, depth)
          ): _*
        )
      case set: Set[_] =>
        Json.arr(
          set
            .map(
              e => encodeAnyToJson(e, depth)
            )
            .toList: _*
        )
      case product: Product =>
        val productElementKeys = productElementNames(product).toList
        val fieldMap = productElementKeys
          .map(
            key => {
              val index = productElementKeys.indexOf(key)
              (key, product.productElement(index))
            }
          )
          .toMap
        encodeAnyToJson(fieldMap)
      case r: Document => encodeAnyToJson(r.toMap)
      case any: Any =>
        if (depth < 256) {
          encodeAnyToJson(any, depth + 1)
        }
        else {
          Json.Null
        }
      case _ =>
        Json.Null
    }
  }

}
