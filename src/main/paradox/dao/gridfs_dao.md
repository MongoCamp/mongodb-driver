# GridFSDAO

## Info

GridFSDAO adds MongoDB [GridFS](https://docs.mongodb.com/manual/core/gridfs/) support.
It provides easy upload, download and metadata handling.

## Usage

A [MongoDatabase](http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoDatabase.html) and a bucket name is needed.

### Create DAO

```scala

  object ImageFilesDAO extends GridFSDAO(database, "images")

```

