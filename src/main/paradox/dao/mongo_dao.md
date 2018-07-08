# MongoDAO

## Info

MongoDAO is the core of this framework.  The [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern simplifies database usage.

The MongoDAO object holds a reference to a [MongoCollection](http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoCollection.html) and adds functions for easy collection handling.

All functions support synchronous result handling (add Result to function name, e.g. drop -> dropResult, insert ->insertResult).

## Features

* @ref:[MongoDAO Base](base.md) (Drop, Index, Count)
* @ref:[MongoDAO CRUD](crud.md) (Create, Insert, Delete)
* @ref:[MongoDAO Search](search.md) (Search, Distinct, Aggregate)

## Usage

A [MongoDatabase](http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoDatabase.html) and a collection name is needed.

### Create DAO

A Type Parameter is used for automatic Document to Class conversion (case classes needs to be registered).

```scala
  object RestaurantDAO extends MongoDAO[Restaurant](database, "restaurants")
```

### Use DAO

```scala

```



