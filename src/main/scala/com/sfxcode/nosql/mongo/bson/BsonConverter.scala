package com.sfxcode.nosql.mongo.bson

import java.lang.reflect.Field
import java.math.BigInteger
import java.time.{ LocalDate, LocalDateTime, ZoneId }
import java.util.Date

import org.bson.BsonValue
import org.joda.time.DateTime
import org.mongodb.scala.Document
import org.mongodb.scala.bson._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.matching.Regex

object BsonConverter {

  var extendedConverter: BaseExtendedConverter = new BaseExtendedConverter()

  def toBson(value: Any): BsonValue = {

    value match {
      case bsonValue: BsonValue => bsonValue
      case option: Option[Any] =>
        if (option.isDefined)
          toBson(option.get)
        else
          BsonNull()
      case b: Boolean => BsonBoolean(b)
      case s: String => BsonString(s)
      case bytes: Array[Byte] => BsonBinary(bytes)
      case r: Regex => BsonRegularExpression(r)
      case d: Date => BsonDateTime(d)
      case dt: DateTime => BsonDateTime(dt.toDate)
      case ld: LocalDate => BsonDateTime(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant))
      case ldt: LocalDateTime => BsonDateTime(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant))
      case oid: ObjectId => BsonObjectId(oid)
      case i: Int => BsonInt32(i)
      case l: Long => BsonInt64(l)
      case bi: BigInt => BsonInt64(bi.longValue())
      case bi: BigInteger => BsonInt64(bi.longValue())
      case d: Double => BsonDouble(d)
      case f: Float => BsonDouble(f)
      case bd: BigDecimal => BsonDouble(bd.doubleValue())
      case doc: Document => BsonDocument(doc)
      case map: Map[Any, Any] =>
        var doc = Document()
        map.keys.foreach(key => {
          val v = map(key)
          doc.+=(key.toString -> toBson(v))
        })
        BsonDocument(doc)
      case map: java.util.Map[Any, Any] =>
        var doc = Document()
        map.keySet().asScala.foreach(key => {
          val v = map.get(key)
          doc.+=(key.toString -> toBson(v))
        })
        BsonDocument(doc)
      case it: Iterable[Any] =>
        BsonArray(it.map(v => toBson(v)))
      case list: java.util.List[Any] =>
        BsonArray(list.asScala.map(v => toBson(v)))
      case _ =>
        extendedConverter.toBsonExtended(value)
    }
  }

  def fromBson(value: BsonValue): Any = {
    value match {
      case b: BsonBoolean => b.getValue
      case b: BsonString => b.getValue
      case b: BsonInt32 => b.getValue
      case b: BsonInt64 => b.getValue
      case b: BsonDouble => b.getValue
      case b: BsonDecimal128 => b.getValue.bigDecimalValue()
    }
  }

}

class BaseExtendedConverter {

  def objectToBson(value: AnyRef): BsonValue = {
    val map: Map[String, Any] = BaseExtendedConverter.membersToMap(value)
    BsonConverter.toBson(map)
  }

  def toBsonExtended(value: Any): BsonValue = {
    value match {
      case v: AnyRef => objectToBson(v)
      case _ =>
        BsonNull()
    }
  }

}

object BaseExtendedConverter {
  private val classRegistry = new mutable.HashMap[Class[_], Map[String, Field]]()

  def membersToMap(v: AnyRef): Map[String, Any] = {
    val result = new mutable.HashMap[String, Any]()

    val clazz = v.getClass

    if (!classRegistry.contains(clazz)) {
      val fields = clazz.getDeclaredFields

      val fieldMap = new mutable.HashMap[String, Field]()

      fields.foreach(field => {
        val name = field.getName
        val real = clazz.getDeclaredField(name)
        fieldMap.+=(name -> real)
        real.setAccessible(true)
        val value = real.get(v)
        result.+=(name -> value)
      })

    } else {
      val fields = classRegistry(clazz)
      fields.keys.foreach(name => {
        val value = fields(name).get(v)
        result.+=(name -> value)
      })
    }

    result.toMap
  }
}

