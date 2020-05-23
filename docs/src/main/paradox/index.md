# simple-mongo

A library for easy usage of the mongo-scala-driver.

Features:

* Easy Database Config with @ref:[provider](database/provider.md) and MongoConfig
* Implement the [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern for simple MongoDB usage (@ref:[MongoDAO](dao/index.md))
* [GridFS](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/tutorials/gridfs/) support (@ref:[GridFSDAO](gridfs/index.md))
* [Reactive Streams](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/getting-started/quick-start-primer/) support (@ref:[Usage in simple-mongo](database/reactive_streams.md))
* Enhanced [BSON](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/bson/) conversion support (@ref:[Bson](database/bson.md))
* @ref:[Relationships](database/relationships.md)

## Additional Documentation

* [mongo-scala-driver](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/)
* [MongoDB](https://docs.mongodb.com/)

## Dependency Setup

Support Scala 2.12 and Scala 2.13.

@@dependency[sbt,Maven,Gradle] {
  group="com.sfxcode.nosql"
  artifact="simple-mongo_2.13"
  version="$project.version$"
}

## Licence

[Apache 2](https://github.com/sfxcode/simple-mongo/blob/master/LICENSE)

## Example: Model

Create custom model classes.

@@snip [model.scala](../../../../src/test/scala/com/sfxcode/nosql/mongo/model/model.scala) { #model_student }

## Example: Database

@@snip [UniversityDatabase.scala](../../../../src/test/scala/com/sfxcode/nosql/mongo/test/UniversityDatabase.scala)

@@@ index

 - [index](database/index.md)
 - [MongoDAO](dao/index.md)
 - [GridFSDAO](gridfs/index.md)
 - [Features](collection/index.md)
 - [server](server.md)
 - [Changes ](changes.md)

@@@
