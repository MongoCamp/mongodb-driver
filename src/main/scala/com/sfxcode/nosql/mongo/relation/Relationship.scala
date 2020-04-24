package com.sfxcode.nosql.mongo.relation

import com.sfxcode.nosql.mongo.relation.RelationCache._
import com.sfxcode.nosql.mongo.{MongoDAO, _}

abstract class Relationship {
  val id: String = hashCode().toString

  def removeFromCache(value: Any): Unit = {
    val key = "%s_%s".format(id, value)
    removeCachedValue(key)
  }
}

case class OneToOneRelationship[A](dao: MongoDAO[A], daoKey: String, useCache: Boolean = true) extends Relationship {

  def relatedRecord(value: Any): Option[A] = {
    val key = "%s_%s".format(id, value)
    if (!useCache || !hasCachedValue(key)) {
      addCachedValue(key, dao.find(daoKey, value).resultOption())
    }
    getCachedValue[Option[A]](key)
  }

}

case class OneToManyRelationship[A](dao: MongoDAO[A], daoKey: String, useCache: Boolean = true) extends Relationship {

  def relatedRecords(value: Any): List[A] = {
    val key = "%s_%s".format(id, value)
    if (!useCache || !hasCachedValue(key)) {
      addCachedValue(key, dao.find(daoKey, value).resultList())
    }
    getCachedValue[List[A]](key)
  }

}
