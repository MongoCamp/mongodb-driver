# Getting Started

A library for easy usage of the mongo-scala-driver.

## Dependency Setup
Support Scala 2.12 and Scala 2.13.
<DependecyGroup/>

## Features:
* Easy Database Config with [provider](database/provider.md) and MongoConfig
* Implement the [DAO](https://en.wikipedia.org/wiki/Data_access_object) Pattern for simple MongoDB usage ([MongoDAO](mongo-dao/index.md))
* [GridFS](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/tutorials/gridfs/) support ([GridFSDAO](gridfs-dao/index.md))
* [Reactive Streams](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/getting-started/quick-start-primer/) support ([Usage in mongocamp](database/reactive-streams.md))
* Enhanced [BSON](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/bson/) conversion support ([Bson](database/bson.md))
* [Relationships](database/relationships.md)

## Example: Model

Create custom model classes.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/model/model.scala#model_student{scala}

## Example: Database
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/test/UniversityDatabase.scala{scala}

## Licence
[Apache 2](https://github.com/MongoCamp/mongodb-driver/blob/master/LICENSE)
