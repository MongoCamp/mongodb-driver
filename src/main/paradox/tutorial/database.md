# Tutorial Part 2 - Database

## Info

The database is an all in one object for our DAO pattern.

## Imports

@@snip [RestaurantDatabase.scala](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #import }


## case classes for document representation

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #case_classes }

## Registry and database

scala-mongo-driver use a registry pattern for deserialization

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #registry }

## Restaurant DAO

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/restaurant/RestaurantDatabase.scala) { #dao }