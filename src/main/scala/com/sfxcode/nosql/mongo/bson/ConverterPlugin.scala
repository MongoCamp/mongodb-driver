package com.sfxcode.nosql.mongo.bson

import java.lang.reflect.Field

import org.bson.BsonValue
import org.mongodb.scala.bson.BsonNull

import scala.collection.mutable

class BaseConverterPlugin extends AbstractConverterPlugin

abstract class AbstractConverterPlugin {

  def customClassList: List[Class[_]] = List()

  def hasCustomClass(v: Any): Boolean = {
    customClassList.foreach(c => {
      if (c.isAssignableFrom(v.getClass)) {
        return true
      }
    })
    false
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

object ClassUtil {
  private val classRegistry =
    new mutable.HashMap[Class[_], Map[String, Field]]()

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
