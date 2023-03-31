# CRUD Functions

## Create

You need a filename, an input stream and some kind of metadata.

Possible Metadata types:

* Document
* Map
* Scala Case Class

Return Observable of ObjectId.

```scala
    ImageFilesDAO.insertOne(filename, stream, metadata)
```

## Update
::: warning Official_MongoDB_Documentation
Do not use GridFS if you need to update the content of the entire file atomically. As an alternative you can store multiple versions of each file and specify the current version of the file in the metadata. You can update the metadata field that indicates “latest” status in an atomic update after uploading the new version of the file, and later remove previous versions if needed.Do not use GridFS if you need to update the content of the entire file atomically. As an alternative you can store multiple versions of each file and specify the current version of the file in the metadata. You can update the metadata field that indicates “latest” status in an atomic update after uploading the new version of the file, and later remove previous versions if needed.
:::

## Delete
GridFSFile will be deleted by a given ObjectID.

With implicit conversion you can use for OID Parameter:

* ObjectID
* GridFSFile
* String

```scala
  // for implicit conversion usage
  import dev.mongocamp.driver.mongodb._
  
  ImageFilesDAO.deleteOne(oid)
```
