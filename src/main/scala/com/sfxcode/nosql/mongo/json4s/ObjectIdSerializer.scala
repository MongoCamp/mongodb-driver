package com.sfxcode.nosql.mongo.json4s

import org.bson.types.ObjectId
import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

/**
 * Created by tom on 19.01.17.
 */
class ObjectIdSerializer extends Serializer[ObjectId] {
  private val ObjectIdClass = classOf[ObjectId]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), ObjectId] = {
    case (TypeInfo(ObjectIdClass, _), json) => json match {
      case JObject(JField("$oid", JString(s)) :: Nil) if ObjectId.isValid(s) =>
        new ObjectId(s)
      case x => throw new MappingException(s"Can't convert $x to ObjectId")
    }
  }

  // not needed
  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }
}
