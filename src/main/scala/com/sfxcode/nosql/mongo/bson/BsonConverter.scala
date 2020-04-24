package com.sfxcode.nosql.mongo.bson

import java.math.BigInteger
import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.Date

import org.mongodb.scala.Document
import org.mongodb.scala.bson.BsonArray.fromIterable
import org.mongodb.scala.bson._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.matching.Regex

object BsonConverter {

  var converterPlugin: AbstractConverterPlugin = new BaseConverterPlugin()

  def toBson(value: Any): BsonValue =
    value match {
      case bsonValue: BsonValue => bsonValue
      case option: Option[Any] =>
        if (option.isDefined) {
          toBson(option.get)
        }
        else {
          BsonNull()
        }
      case v: Any if converterPlugin.hasCustomClass(v) =>
        converterPlugin.toBson(v)
      case b: Boolean         => BsonBoolean(b)
      case s: String          => BsonString(s)
      case c: Char            => BsonString(c.toString)
      case bytes: Array[Byte] => BsonBinary(bytes)
      case r: Regex           => BsonRegularExpression(r)
      case d: Date            => BsonDateTime(d)
      case ld: LocalDate =>
        BsonDateTime(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant))
      case ldt: LocalDateTime =>
        BsonDateTime(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant))
      case oid: ObjectId            => BsonObjectId(oid)
      case i: Int                   => BsonInt32(i)
      case l: Long                  => BsonInt64(l)
      case bi: BigInt               => BsonInt64(bi.toLong)
      case bi: BigInteger           => BsonInt64(bi.longValue())
      case d: Double                => BsonDouble(d)
      case f: Float                 => BsonDouble(f)
      case bd: BigDecimal           => BsonDecimal128.apply(bd)
      case bd: java.math.BigDecimal => BsonDecimal128.apply(bd)
      case doc: Document            => BsonDocument(doc)
      case map: scala.collection.Map[_, _] =>
        var doc = Document()
        map.keys.foreach { key =>
          val v = map(key)
          doc.+=(key.toString -> toBson(v))
        }
        BsonDocument(doc)
      case map: java.util.Map[_, _] =>
        var doc = Document()
        map
          .keySet()
          .asScala
          .foreach { key =>
            val v = map.get(key)
            doc.+=(key.toString -> toBson(v))
          }
        BsonDocument(doc)
      case it: Iterable[Any] =>
        fromIterable(it.map(v => toBson(v)))
      case list: java.util.List[_] =>
        fromIterable(list.asScala.map(v => toBson(v)))
      case v: AnyRef => converterPlugin.objectToBson(v)
      case _ =>
        BsonNull()
    }

  def fromBson(value: BsonValue): Any =
    value match {

      case b: BsonBoolean           => b.getValue
      case s: BsonString            => s.getValue
      case bytes: BsonBinary        => bytes.getData
      case r: BsonRegularExpression => r.getPattern
      case d: BsonDateTime          => new Date(d.getValue)
      case d: BsonTimestamp         => new Date(d.getTime)
      case oid: BsonObjectId        => oid.getValue
      case i: BsonInt32             => i.getValue
      case l: BsonInt64             => l.getValue
      case d: BsonDouble            => d.doubleValue()
      case d: BsonDecimal128        => d.getValue.bigDecimalValue()
      case doc: BsonDocument        => Document(doc)
      case array: BsonArray =>
        array.getValues.asScala.toList.map(v => fromBson(v))
      case n: BsonNull => null
      case _           => value
    }

  def asMap(document: Document): Map[String, Any] = {
    val result = new mutable.HashMap[String, Any]()
    document.keySet.foreach { key =>
      val value = fromBson(document(key))

      value match {
        case d: Document =>
          result.+=(key -> asMap(d))
        case _ => result.+=(key -> value)
      }
    }
    result.toMap
  }

  def asMapList(documents: List[Document]): List[Map[String, Any]] = {
    val result = new mutable.ArrayBuffer[Map[String, Any]]()
    documents.foreach { document =>
      result.+=(asMap(document))
    }
    result.toList
  }

}
