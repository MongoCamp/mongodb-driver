package com.sfxcode.nosql.mongo.restaurant

import com.sfxcode.nosql.mongo.MongoDAO
import org.joda.time.DateTime
import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId

// Custom Serializer for Joda Time needed
import com.sfxcode.nosql.mongo.json4s.JodaBsonSerializer._

/**
 * import mongodb restaurants sample data
 */
object RestaurantDatabase {

  case class Address(street: String, building: String, zipcode: String, coord: List[Double])

  case class Grade(date: DateTime, grade: String, score: Int)

  case class Restaurant(restaurant_id: String, name: String, borough: String, cuisine: String,
    grades: List[Grade], address: Address, _id: ObjectId = new ObjectId())

  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("test")

  val restaurantCollection: MongoCollection[Document] = database.getCollection("restaurants")

  object RestaurantDAO extends MongoDAO[Restaurant](restaurantCollection)

}
