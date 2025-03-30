package dev.mongocamp.driver.mongodb.relation

import dev.mongocamp.driver.mongodb.relation.RelationCache.addCachedValue
import dev.mongocamp.driver.mongodb.relation.RelationCache.getCachedValue
import dev.mongocamp.driver.mongodb.relation.RelationCache.hasCachedValue
import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.MongoDAO
import io.circe.Decoder

case class OneToOneRelationship[A](dao: MongoDAO[A], daoKey: String, useCache: Boolean = true) extends Relationship {

  def relatedRecord(value: Any): Option[A] = {
    val key = "%s_%s".format(id, value)
    if (!useCache || !hasCachedValue(key)) {
      addCachedValue(key, dao.find(daoKey, value).resultOption())
    }
    getCachedValue[Option[A]](key)
  }

}
