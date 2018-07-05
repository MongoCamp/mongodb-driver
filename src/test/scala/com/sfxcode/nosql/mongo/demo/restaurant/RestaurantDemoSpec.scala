package com.sfxcode.nosql.mongo.demo.restaurant

import com.sfxcode.nosql.mongo.demo.restaurant.RestaurantDemoDatabase._
import org.specs2.mutable.Specification

class RestaurantDemoSpec extends Specification with RestaurantDemoDatabaseFunctions {

  "RestaurantDemo" should {

    "find restaurant by name in" in {

      val restaurantSearch = findRestaurantByName("Dj Reynolds Pub And Restaurant")
      restaurantSearch must beSome[Restaurant]
      val restaurant = restaurantSearch.get
      restaurant.borough must be equalTo "Manhattan"
    }
  }

}
