package com.sfxcode.nosql.mongo.json4s

import java.util.Date

import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

class DateSerializer(fieldName: String = "$date") extends Serializer[Date] {
  private val DateClass = classOf[Date]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Date] = {
    case (TypeInfo(DateClass, _), json) => json match {
      case JObject(JField(`fieldName`, JInt(s)) :: Nil) =>
        new Date(s.longValue())
      case x => throw new MappingException(s"Can't convert $x to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }
}
