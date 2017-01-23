package com.sfxcode.nosql.mongo.json4s

import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

/**
 * Created by tom on 19.01.17.
 */
class LongSerializer extends Serializer[Long] {
  private val LongClass = classOf[Long]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Long] = {
    case (TypeInfo(LongClass, _), json) => json match {
      case JObject(JField("$numberLong", JString(s)) :: Nil) =>
        s.toLong
      case x => throw new MappingException(s"Can't convert $x to Long")
    }
  }

  // not needed
  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }

}
