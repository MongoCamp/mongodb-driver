package com.sfxcode.nosql.mongo.restaurant

import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.json4s.DefaultBsonSerializer._
import org.mongodb.scala._

/**
 * import mongodb restaurants sample data
 */
object RestaurantDatabase {

  case class Restaurant(name: String, borough: String)

  val mongoClient: MongoClient = MongoClient()

  val database: MongoDatabase = mongoClient.getDatabase("test")

  val restaurantCollection: MongoCollection[Document] = database.getCollection("restaurants")

  object RestaurantDAO extends MongoDAO[Restaurant](restaurantCollection)

}
