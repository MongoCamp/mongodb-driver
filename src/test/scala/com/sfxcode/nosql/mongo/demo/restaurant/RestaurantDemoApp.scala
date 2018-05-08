package com.sfxcode.nosql.mongo.demo.restaurant

// #import
// static import of needed DAO objects
import com.sfxcode.nosql.mongo.demo.restaurant.RestaurantDemoDatabase._

// static import of mongo package object for needed implicits
import com.sfxcode.nosql.mongo._
// #import

// #app

object RestaurantDemoApp extends App {
  // find specific restaurant key and value as Option Result
  val restaurant = RestaurantDAO.find("name", "Dj Reynolds Pub And Restaurant")

  println(restaurant.get)

  private val filter = Map("address.zipcode" -> "10075", "cuisine" -> "Italian")
  // find restaurants by filter - use implicit map to document conversion
  // as List result
  val restaurants: List[Restaurant] = RestaurantDAO.find(filter)

  restaurants.sortBy(r => r.name).foreach(r => println(r.name))

}

// #app
