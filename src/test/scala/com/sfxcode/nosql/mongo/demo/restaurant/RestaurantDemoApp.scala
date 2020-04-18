package com.sfxcode.nosql.mongo.demo.restaurant

// #app

object RestaurantDemoApp extends App with RestaurantDemoDatabaseFunctions {

  // find specific restaurant by name as Option Result
  val restaurant = findRestaurantByName("Dj Reynolds Pub And Restaurant")

  println(restaurant)

  // use count function
  println(restaurantsSize)

  // find restaurants by filter
  private val filter = Map("address.zipcode" -> "10075", "cuisine" -> "Italian")
  val restaurants = findAllRestaurants(filter)

  restaurants.sortBy(r => r.name).foreach(r => println(r.name))

}

// #app
