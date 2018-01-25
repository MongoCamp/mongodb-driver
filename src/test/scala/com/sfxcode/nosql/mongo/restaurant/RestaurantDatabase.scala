package com.sfxcode.nosql.mongo.restaurant

// #import
import java.util.Date

import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._
// #import

/**
 * import mongodb restaurants sample data
 */
object RestaurantDatabase {

  // #case_classes
  case class Address(street: String, building: String,
    zipcode: String, coord: List[Double])

  case class Grade(date: Date, grade: String, score: Int)

  case class Restaurant(restaurant_id: String, name: String,
    borough: String, cuisine: String,
    grades: List[Grade], address: Address,
    _id: ObjectId = new ObjectId())

  // #case_classes

  // #registry
  private val registry = fromProviders(
    classOf[Restaurant],
    classOf[Address], classOf[Grade])

  val database = DatabaseProvider("test", registry)

  // #registry

  // #dao
  object RestaurantDAO extends MongoDAO[Restaurant](database, "restaurants")

  // #dao
}

