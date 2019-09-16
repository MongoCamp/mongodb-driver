# simple-mongo

A library for easy usage of the mongo-scala-driver.

## MongoDB Support

Support MongoDB 2.6 to 4.2.

## Features

* Easy Database setup
* DAO Pattern for collection
* DAO Pattern for GridFS
* Implicit conversions for Document, Bson, ObjectID ...
* Example Apps

## Documentation

Documentation can be found [here](https://sfxcode.github.io/simple-mongo/).

## Version

Scala Version is 2.13.x / 2.12.x.

## Travis

[![Build Status](https://travis-ci.org/sfxcode/simple-mongo.svg?branch=master)](https://travis-ci.org/sfxcode/simple-mongo)

## Download

[ ![Download](https://api.bintray.com/packages/sfxcode/maven/simple-mongo/images/download.svg) ](https://bintray.com/sfxcode/maven/simple-mongo/_latestVersion)

## Licence

[Apache 2 License](https://github.com/sfxcode/simple-mongo/blob/master/LICENSE).


## Usage

Add following lines to your build.sbt

```
resolvers += "sxfcode Bintray Repo" at "https://dl.bintray.com/sfxcode/maven/"

libraryDependencies += "com.sfxcode.nosql" %% "simple-mongo" % "1.6.5"

```

Define MongoDB Connection and [DAO](https://en.wikipedia.org/wiki/Data_access_object) objects for automatic case class conversion.


```scala

import java.util.Date

import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider
import org.bson.codecs.configuration.CodecRegistries._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.Macros._

/**
 * import mongodb restaurants sample data
 */
object RestaurantDatabase {

  case class Address(street: String, building: String, zipcode: String, coord: List[Double])

  case class Grade(date: Date, grade: String, score: Int)

  case class Restaurant(restaurant_id: String, name: String, borough: String, cuisine: String,
    grades: List[Grade], address: Address, _id: ObjectId = new ObjectId())

  private val registry = fromProviders(classOf[Restaurant], classOf[Address], classOf[Grade])

  val provider = DatabaseProvider("test", registry)

  object RestaurantDAO extends MongoDAO[Restaurant](provider, "restaurants")

}


```

Import the database object and execute some find and CRUD functions on the DAO object ...

```scala

import com.sfxcode.nosql.mongo.demo.restaurant.RestaurantDemoDatabase._
import com.sfxcode.nosql.mongo._

trait RestaurantDemoDatabaseFunctions {

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

```


Use the mongodb functions in your app ...

```scala
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

```

Write some spec tests ...

```scala

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

```
## Supporters

JetBrains is supporting this open source project with:

[![Intellij IDEA](http://www.jetbrains.com/img/logos/logo_intellij_idea.png)](http://www.jetbrains.com/idea/)





