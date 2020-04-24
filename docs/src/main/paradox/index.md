# simple-mongo

A library for easy usage of the mongo-scala-driver.

Features:

* Easy Config with @ref:[DatabaseProvider](database_provider.md)
* Implement the [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern for simple MongoDB database usage (@ref:[MongoDAO](dao/index.md))
* Enhanced [BSON](http://mongodb.github.io/mongo-scala-driver/2.2/bson/) conversion support (@ref:[Converter](features/converter.md))

* [GridFS](https://docs.mongodb.com/manual/core/gridfs/) support (@ref:[GridFSDAO](gridfs/index.md))
* @ref:[Relationships](features/relationships.md)

## Additional Documentation

* [mongo-scala-driver](http://mongodb.github.io/mongo-scala-driver/2.2/)
* [MongoDB](https://docs.mongodb.com/)


## Dependency Setup

Support Scala 2.12 and Scala 2.13.

@@dependency[sbt,Maven,Gradle] {
  group="com.sfxcode.nosql"
  artifact="simple-mongo_2.12"
  version="$project.version$"
}

## Licence

[Apache 2](https://github.com/sfxcode/simple-mongo/blob/master/LICENSE)

@@@ index

 - [Database Provider](database_provider.md)
 - [MongoDAO](dao/index.md)
 - [GridFSDAO](gridfs/index.md)
 - [Features](features/index.md)
 - [Changes ](changes.md)

@@@
