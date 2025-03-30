package dev.mongocamp.driver

import dev.mongocamp.driver.mongodb.operation.ObservableIncludes
import org.bson.types.ObjectId
import org.bson.BsonValue
import org.mongodb.scala.gridfs.GridFSFile
import org.mongodb.scala.gridfs.GridFSFindObservable
import org.mongodb.scala.Observable
import org.mongodb.scala.ObservableImplicits
import scala.language.implicitConversions
trait MongoImplicits extends ObservableIncludes with ObservableImplicits {

  implicit def observableToResult[T](obs: Observable[T]): T = obs.result()

  implicit def findObservableToResultList[T](obs: Observable[T]): List[T] = obs.resultList()

  implicit def findObservableToResultOption[T](obs: Observable[T]): Option[T] = obs.resultOption()

  // gridfs-dao

  implicit def gridFSFindObservableToFiles(observable: GridFSFindObservable): List[GridFSFile] = observable.resultList()

  implicit def gridFSFileToObjectId(file: GridFSFile): ObjectId = file.getObjectId

  implicit def gridFSFileToBSonIdValue(file: GridFSFile): BsonValue = file.getId

}
