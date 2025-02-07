package dev.mongocamp.driver.mongodb.relation

import dev.mongocamp.driver.mongodb.relation.RelationCache.{ addCachedValue, getCachedValue, hasCachedValue }
import dev.mongocamp.driver.mongodb.{ GenericObservable, MongoDAO }
import io.circe.Decoder

case class OneToManyRelationship[A](dao: MongoDAO[A], daoKey: String, useCache: Boolean = true) extends Relationship {

  def relatedRecords(value: Any): List[A] = {
    val key = "%s_%s".format(id, value)
    if (!useCache || !hasCachedValue(key)) {
      addCachedValue(key, dao.find(daoKey, value).resultList())
    }
    getCachedValue[List[A]](key)
  }

}
