# simple-mongo

A library for easy usage of the mongo-scala-driver.

Implement the [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern for easy database usage.

Enhanced [BSON](http://mongodb.github.io/mongo-scala-driver/2.2/bson/) conversion support.

[GridFS](https://docs.mongodb.com/manual/core/gridfs/) support.

## Additional Documentation


Documentation for [mongo-scala-driver](http://mongodb.github.io/mongo-scala-driver/2.2/)

Documentation for [MongoDB](https://docs.mongodb.com/)


## Dependency Setup

@@dependency[sbt,Maven,Gradle] {
  group="com.sfxcode.nosql"
  artifact="simple-mongo_2.12"
  version="$app-version$"
}

## Licence

[Apache 2](https://github.com/sfxcode/simple-mongo/blob/master/LICENSE)

@@@ index

 - [Tutorial Setup](tutorial/setup.md)
 - [Tutorial Database](tutorial/database.md)
 - [Tutorial Functions](tutorial/functions.md)
 - [Tutorial Application](tutorial/application.md)
 - [MongoDAO](dao/mongo_dao.md)
 - [MongoDAO Base](dao/base.md)
 - [MongoDAO CRUD](dao/crud.md)
 - [MongoDAO Search](dao/search.md)
 - [GridFSDAO](dao/gridfs_dao.md)
 - [Feature Aggregation](features/aggregation.md)
 - [Feature Relationships](features/relationships.md)
 - [Changes ](changes.md)

@@@
