# Mongocamp MongoDB Driver

A library for easy usage of the mongo-scala-driver (4.5.x). Full MongoDB Functionality in Scala with a few lines of code.

## MongoDB Support

Support MongoDB 2.6 to 5.0.x.

## Features

* Easy Database setup with Config file
* Compressor Support
* Database Commands, Changes Stream, ...
* DAO Pattern for collection
* DAO Pattern for GridFS
* GridFS: Upload from InputStream, Download to OutputStream
* Implicit conversions for Document, Bson, ObjectID ...
* Reactive Streams Support
* Json loading from File, Conversion to plain Json
* Local Server Support [mongo-java-server](https://github.com/bwaldvogel/mongo-java-server)
* Collection Sync
* ...

## Documentation

Documentation can be found [here](https://mongodb-driver.mongocamp.dev/).

## Version

Scala Version is 2.13.x / 2.12.x.

## CI


## Download

## Licence

[Apache 2 License](https://github.com/mongocamp/mongocamp-driver-mongodb/blob/master/LICENSE).


## Usage

Add following lines to your build.sbt (replace x.x with the actual Version)

```

libraryDependencies += "dev.mongocamp" %% "mongodb-driver" % "2.x.x"

```

Define MongoDB Connection and [DAO](https://en.wikipedia.org/wiki/Data_access_object) objects for automatic case class conversion.

```scala

import java.util.Date

import dev.mongocamp.driver.mongodb.MongoDAO
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
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

import dev.mongocamp.driver.mongodb.demo.restaurant.RestaurantDemoDatabase._
import dev.mongocamp.driver.mongodb._

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

import dev.mongocamp.driver.mongodb.demo.restaurant.RestaurantDemoDatabase._
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

## Run Tests
```shell
  docker run --publish 27017:27017  mongocamp/mongodb:latest;
  sbt test;
```

## Supporters

JetBrains is supporting this open source project with:

[![Intellij IDEA](http://www.jetbrains.com/img/logos/logo_intellij_idea.png)](http://www.jetbrains.com/idea/)





