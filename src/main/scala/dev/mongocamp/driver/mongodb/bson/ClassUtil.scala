package dev.mongocamp.driver.mongodb.bson

import java.lang.reflect.Field
import scala.collection.mutable

object ClassUtil {
  private val classRegistry =
    new mutable.HashMap[Class[_], Map[String, Field]]()

  def membersToMap(v: AnyRef): Map[String, Any] = {
    val result = new mutable.HashMap[String, Any]()

    val clazz = v.getClass

    if (!classRegistry.contains(clazz)) {
      val fields = clazz.getDeclaredFields

      val fieldMap = new mutable.HashMap[String, Field]()

      fields.foreach { field =>
        val name = field.getName
        val real = clazz.getDeclaredField(name)
        fieldMap.+=(name -> real)
        real.setAccessible(true)
        val value = real.get(v)
        result.+=(name -> value)
      }

    }
    else {
      val fields = classRegistry(clazz)
      fields.keys.foreach { name =>
        val value = fields(name).get(v)
        result.+=(name -> value)
      }
    }

    result.toMap
  }
}
