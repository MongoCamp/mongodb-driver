package dev.mongocamp.driver.mongodb.json

import io.circe.Encoder
import io.circe.Json
import java.time.Instant
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import org.bson.types.ObjectId
import org.joda.time.DateTime
import org.mongodb.scala.Document
import scala.concurrent.duration.Duration

trait CirceConverter {

  def productElementNames(internalProduct: Product): Iterator[String] = {
    internalProduct.productElementNames
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

object CirceConverter extends CirceConverter
