package dev.mongocamp.driver.mongodb

import org.bson.conversions.Bson
import org.mongodb.scala.model.Sorts.ascending
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.model.Sorts.orderBy

object Sort extends Sort

trait Sort {

  def sortByKey(key: String, sortAscending: Boolean = true): Bson =
    if (sortAscending) {
      orderBy(ascending(key))
    }
    else {
      orderBy(descending(key))
    }

}
