package com.sfxcode.nosql.mongo.demo.restaurant

// #import
// static import of needed DAO objects
import com.sfxcode.nosql.mongo.demo.restaurant.RestaurantDemoDatabase._

// static import of mongo package object for needed implicits
import com.sfxcode.nosql.mongo._
// #import

trait RestaurantDemoDatabaseFunctions {

  // #trait

  /**
    * single result with implicit conversion to Entity Option
    */
  def findRestaurantByName(name: String): Option[Restaurant] =
    RestaurantDAO.find("name", name)

  def restaurantsSize: Long = RestaurantDAO.count()

  /**
    * result with implicit conversion to List of Entities
    */
  def findAllRestaurants(filterValues: Map[String, Any] = Map()): List[Restaurant] =
    RestaurantDAO.find(filterValues)

  // #trait

}
