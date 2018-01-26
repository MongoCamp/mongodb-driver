# Tutorial Part 2 - Database

## Info

The database provides static import to our [DAO](https://en.wikipedia.org/wiki/Data_access_object) objects.

## Imports

@@snip [RestaurantDatabase.scala](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #import }


## case classes for document representation

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #case_classes }

## Registry and database

scala-mongo-driver use a registry pattern for deserialization

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #registry }

## Restaurant DAO

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #dao }

## Usage
* Create Database object

```scala

object RestaurantDatabase {

// case classes ...

// registry and database setup ...

object RestaurantDAO extends MongoDAO[Restaurant](database, "restaurants")

// more DAO objects
}
```

* Static Import and Application

```scala
   // static import of needed DAO objects
   import my.namespace.RestaurantDatabase._
   
   // ... some code
   
```