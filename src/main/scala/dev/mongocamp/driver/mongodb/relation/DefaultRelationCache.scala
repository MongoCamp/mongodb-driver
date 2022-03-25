package dev.mongocamp.driver.mongodb.relation

import scala.collection.mutable

class DefaultRelationCache extends RelationCaching {
  val cachedRelations = new mutable.HashMap[String, AnyRef]()

  def addCachedValue(key: String, value: AnyRef): Unit =
    cachedRelations.+=(key -> value)

  def getCachedValue[B <: AnyRef](key: String): B =
    cachedRelations(key).asInstanceOf[B]

  def hasCachedValue(key: String): Boolean =
    cachedRelations.contains(key)

  def removeCachedValue(key: String): Unit =
    cachedRelations.remove(key)

}
