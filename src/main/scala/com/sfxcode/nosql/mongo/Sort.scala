package com.sfxcode.nosql.mongo

import org.bson.conversions.Bson
import org.mongodb.scala.model.Sorts.{ ascending, descending, orderBy }

object Sort {

  def sortByKey(key: String, sortAscending: Boolean = true): Bson = {
    if (sortAscending)
      orderBy(ascending(key))
    else
      orderBy(descending(key))
  }

}
