package dev.mongocamp.driver.mongodb.bson

import java.util.Date

import org.bson.types.ObjectId

/** Created by tom on 22.01.17.
  */
case class Base(
    int: Int,
    Long: Long,
    float: Float,
    double: Double,
    string: String,
    date: Date = new Date(),
    option: Option[ObjectId] = Some(new ObjectId())
)

object Base {
  def apply(): Base = new Base(1, 2, 3, 4, "test")
}
