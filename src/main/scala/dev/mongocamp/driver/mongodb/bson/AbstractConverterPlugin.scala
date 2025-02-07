package dev.mongocamp.driver.mongodb.bson

import org.bson.BsonValue
import org.mongodb.scala.bson.BsonNull

abstract class AbstractConverterPlugin {

  def customClassList: List[Class[_]] = {
    List()
  }

  def hasCustomClass(v: Any): Boolean = {
    customClassList.exists(c => c.isAssignableFrom(v.getClass))
  }

  def objectToBson(value: AnyRef): BsonValue = {
    val map: Map[String, Any] = ClassUtil.membersToMap(value)
    BsonConverter.toBson(map)
  }

  def toBson(value: Any): BsonValue =
    value match {
      case _ =>
        BsonNull()
    }
}
