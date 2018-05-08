package com.sfxcode.nosql.mongo.relation

object RelationCache extends RelationCaching {
  var relationCaching: RelationCaching = new DefaultRelationCache

  override def addCachedValue(key: String, value: AnyRef): Unit = relationCaching.addCachedValue(key, value)

  override def getCachedValue[B <: AnyRef](key: String): B = relationCaching.getCachedValue[B](key)

  override def hasCachedValue(key: String): Boolean = relationCaching.hasCachedValue(key)

  override def removeCachedValue(key: String): Unit = relationCaching.removeCachedValue(key)
}
