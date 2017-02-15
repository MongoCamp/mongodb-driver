package com.sfxcode.nosql.mongo.restaurant

import com.sfxcode.nosql.mongo.restaurant.RestaurantDatabase.RestaurantDAO

object RestaurantApp extends App {

  val restaurants = RestaurantDAO.find("name", "Dj Reynolds Pub And Restaurant")

  println(restaurants)

}
