package dev.mongocamp.driver.mongodb.gridfs

import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import org.mongodb.scala.Document
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.gridfs.GridFSFindObservable
import org.mongodb.scala.model.Filters.equal

abstract class Search extends Base {

  def find(filter: Bson = Document(), sort: Bson = Document(), limit: Int = 0): GridFSFindObservable =
    if (limit > 0)
      gridfsBucket.find(filter).sort(sort).limit(0)
    else
      gridfsBucket.find(filter).sort(sort)

  def findById(oid: ObjectId): GridFSFindObservable = find(equal(DatabaseProvider.ObjectIdKey, oid))

  def find(key: String, value: Any): GridFSFindObservable =
    find(equal(key, value))

  def findByMetadataValue(key: String, value: Any): GridFSFindObservable =
    find(createMetadataKey(key), value)

}
