package com.sfxcode.nosql.mongo.json4s

import java.math.BigInteger

import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.reflect.TypeInfo

/**
 * Created by tom on 19.01.17.
 */
class BigIntSerializer extends Serializer[BigInt] {
  private val BigIntClass = classOf[BigInt]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), BigInt] = {
    case (TypeInfo(BigIntClass, _), json) => json match {
      case JObject(JField("$numberLong", JString(s)) :: Nil) =>
        new BigInt(BigInteger.valueOf(s.toLong))
      case x => throw new MappingException(s"Can't convert $x to ObjectId")
    }
  }

  // not needed
  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: Any => JObject()
  }

}
