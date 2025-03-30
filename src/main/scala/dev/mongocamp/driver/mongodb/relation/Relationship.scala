package dev.mongocamp.driver.mongodb.relation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.relation.RelationCache._
import dev.mongocamp.driver.mongodb.MongoDAO

abstract class Relationship {
  val id: String = hashCode().toString

  def removeFromCache(value: Any): Unit = {
    val key = "%s_%s".format(id, value)
    removeCachedValue(key)
  }
}
