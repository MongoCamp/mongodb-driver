package com.sfxcode.nosql.mongo.demo.restaurant

import com.sfxcode.nosql.mongo.demo.restaurant.RestaurantDemoDatabase._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll
import com.sfxcode.nosql.mongo._

class RestaurantDemoSpec extends Specification with RestaurantDemoDatabaseFunctions with BeforeAll {

  "RestaurantDemo" should {

    "find restaurant by name in" in {

      val restaurantSearch = findRestaurantByName("Dj Reynolds Pub And Restaurant")
      restaurantSearch must beSome[Restaurant]
      val restaurant = restaurantSearch.get
      restaurant.borough must be equalTo "Manhattan"
    }
  }

  override def beforeAll(): Unit = {
    // needed if no restaurants imported
    val count = restaurantsSize
    val testRestaurant: Restaurant = RestaurantReader.testData

    if (count == 0) {
      RestaurantDAO.insertOne(testRestaurant).headResult()
    }
  }
}
