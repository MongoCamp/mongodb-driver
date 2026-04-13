package dev.mongocamp.driver.mongodb.bson

import org.bson.BsonValue
import org.mongodb.scala.bson.BsonNull

abstract class AbstractConverterPlugin {

  def customClassList: List[Class[_]] = {
    List()
  }

  def hasCustomClass(v: Any): Boolean = {
    customClassList.exists(
      c => c.isAssignableFrom(v.getClass)
    )
  }

  def objectToBson(value: AnyRef): BsonValue = {
    val rawMap: Map[String, Any] = ClassUtil.membersToMap(value)
    val map = rawMap.filter {
      case (k, v) => k != "MODULE$" && (v == null || !(v.asInstanceOf[AnyRef] eq value))
    }
    if (map.isEmpty && value.isInstanceOf[Product]) {
      org.mongodb.scala.bson.BsonString(value.asInstanceOf[Product].productPrefix)
    }
    else {
      BsonConverter.toBson(map)
    }
  }

  def toBson(value: Any): BsonValue =
    value match {
      case _ =>
        BsonNull()
    }
}
