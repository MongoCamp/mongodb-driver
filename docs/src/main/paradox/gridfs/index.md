# GridFSDAO

## Info

GridFSDAO adds MongoDB [GridFS](https://docs.mongodb.com/manual/core/gridfs/) support.

It provides easy upload, download and metadata handling.

Sometimes also normal collections can be helpful for storing data.

@@@ note { title=Official_MongoDB_Documentation }

Furthermore, if your files are all smaller than the 16 MB BSON Document Size limit, consider storing each file in a single document instead of using GridFS. You may use the BinData data type to store the binary data. See your drivers documentation for details on using BinData.Furthermore, if your files are all smaller than the 16 MB BSON Document Size limit, consider storing each file in a single document instead of using GridFS. You may use the BinData data type to store the binary data. See your drivers documentation for details on using BinData.

@@@

## Usage

A [MongoDatabase](http://mongodb.github.io/mongo-scala-driver/2.3/scaladoc/org/mongodb/scala/MongoDatabase.html) and a bucket name is needed.

### Create DAO

```scala

  /**
  * use bucket name fs
  */
  object ImageFilesDAO extends GridFSDAO(database)

  /**
  * use bucket name images
  */
  object ImageFilesDAO extends GridFSDAO(database, "images")

```

@@@@ index

 - [CRUD](crud.md)
 - [Metadata](metadata.md)

@@@@
