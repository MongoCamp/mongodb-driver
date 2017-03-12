package com.sfxcode.nosql.mongo.restaurant

import com.sfxcode.nosql.mongo.restaurant.RestaurantDatabase._

import com.sfxcode.nosql.mongo._

object RestaurantApp extends App {

  val restaurant: Option[Restaurant] = RestaurantDAO.findOne("name", "Dj Reynolds Pub And Restaurant")

  println(restaurant.get.grades)

  val restaurants = RestaurantDAO.find(Map("address.zipcode"->"10075", "cuisine"->"Italian"))

  restaurants.sortBy(r => r.name).foreach(r => println(r.name))

}
