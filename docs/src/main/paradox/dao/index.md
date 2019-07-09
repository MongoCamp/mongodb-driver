# MongoDAO

## Info

MongoDAO is the core of this framework.  The [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern simplifies database usage.

The MongoDAO object holds a reference to a [MongoCollection](http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoCollection.html) and adds functions for easy collection handling.


## Features

* @ref:[MongoDAO Base](base.md) (Drop, Index, Count)
* @ref:[MongoDAO CRUD](crud.md) (Create, Insert, Delete)
* @ref:[MongoDAO Search](search.md) (Search, Distinct, Aggregate)

## Usage

A DatabaseProvider is needed.

```scala

private val registry = fromProviders(classOf[Restaurant])

val provider = DatabaseProvider("database", registry)
```

### Create DAO

A Type Parameter is used for automatic Document to Class conversion (case classes needs to be registered).

```scala
  object RestaurantDAO extends MongoDAO[Restaurant](provider, "restaurants")
```

### Use DAO

```scala
 import com.sfxcode.nosql.mongo._

  def restaurantsSize: Long = RestaurantDAO.count()

  def findAllRestaurants:List[Restaurant] = RestaurantDAO.find()
```

@@@@ index

 - [Base](base.md)
 - [CRUD](crud.md)
 - [Search](search.md)

@@@@
