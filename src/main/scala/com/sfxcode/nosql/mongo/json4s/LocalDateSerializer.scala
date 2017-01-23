package com.sfxcode.nosql.mongo.json4s

import java.time.{ LocalDate, ZoneId }
import java.util.Date

import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

class LocalDateSerializer(fieldName: String = "$date") extends Serializer[LocalDate] {
  private val LocalDateClass = classOf[LocalDate]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), LocalDate] = {
    case (TypeInfo(LocalDateClass, _), json) => json match {
      case JObject(JField(`fieldName`, JInt(s)) :: Nil) =>
        new Date(s.longValue()).toInstant.atZone(ZoneId.systemDefault()).toLocalDate
      case x => throw new MappingException(s"Can't convert $x to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }
}
