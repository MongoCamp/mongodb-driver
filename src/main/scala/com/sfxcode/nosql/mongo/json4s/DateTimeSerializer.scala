package com.sfxcode.nosql.mongo.json4s

import org.joda.time.DateTime
import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

class DateTimeSerializer(fieldName: String = "$date") extends Serializer[DateTime] {
  private val DateTimeClass = classOf[DateTime]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), DateTime] = {
    case (TypeInfo(DateTimeClass, _), json) => json match {
      case JObject(JField(`fieldName`, JInt(s)) :: Nil) =>
        new DateTime(s.longValue())
      case x => throw new MappingException(s"Can't convert $x to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }
}
