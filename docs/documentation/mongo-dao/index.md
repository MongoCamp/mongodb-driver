# MongoDAO

## Info

MongoDAO is the core of this framework.  The [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern simplifies database usage.

The MongoDAO object holds a reference to a [MongoCollection](http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoCollection.html) and adds functions for easy collection handling.


## Features

* [MongoDAO Base](base.md) (Drop, Index, Count)
* [MongoDAO CRUD](crud.md) (Create, Insert, Delete)
* [MongoDAO Search](search.md) (Search, Distinct, Aggregate)

## Usage

A DatabaseProvider is needed.

```scala
val provider = DatabaseProvider.fromPath("dev.mongocamp")
```

### Create DAO

::: warning
Since Version 3.0.0 with Scala 3 support, we use Circe for automatic case class conversion.
In the most cases, you can use the generic auto import. More Information about Circe can be found [here](https://circe.github.io/circe/codecs/auto-derivation.html).
:::

A Type Parameter is used for automatic Document to Class conversion. 

```scala
import dev.mongocamp.driver.mongodb.json._
import io.circe.generic.auto._
object RestaurantDAO extends MongoDAO[Restaurant](provider, "restaurants")
```

### Use DAO

```scala
import dev.mongocamp.driver.mongodb._

def restaurantsSize: Long = RestaurantDAO.count()

def findAllRestaurants:List[Restaurant] = RestaurantDAO.find()
```
