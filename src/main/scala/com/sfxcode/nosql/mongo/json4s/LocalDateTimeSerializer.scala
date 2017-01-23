package com.sfxcode.nosql.mongo.json4s

import java.time.{ LocalDateTime, ZoneId }
import java.util.Date

import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

class LocalDateTimeSerializer(fieldName: String = "$date") extends Serializer[LocalDateTime] {
  private val LocalDateTimeClass = classOf[LocalDateTime]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), LocalDateTime] = {
    case (TypeInfo(LocalDateTimeClass, _), json) => json match {
      case JObject(JField(`fieldName`, JInt(s)) :: Nil) =>
        LocalDateTime.ofInstant(new Date(s.longValue()).toInstant, ZoneId.systemDefault())
      case x => throw new MappingException(s"Can't convert $x to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }
}
