package dev.mongocamp.driver.mongodb

import java.nio.charset.Charset

import better.files.File
import dev.mongocamp.driver.mongodb.bson.{ BsonConverter, DocumentHelper }
import dev.mongocamp.driver.mongodb.database.{ ChangeObserver, CollectionStatus, DatabaseProvider }
import dev.mongocamp.driver.mongodb.operation.Crud
import org.bson.json.JsonParseException
import org.mongodb.scala.model.Aggregates.project
import org.mongodb.scala.model.Projections
import org.mongodb.scala.{ BulkWriteResult, Document, MongoCollection, Observable, SingleObservable }

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Accumulators._

/** Created by tom on 20.01.17.
  */
abstract class MongoDAO[A](provider: DatabaseProvider, collectionName: String)(implicit ct: ClassTag[A]) extends Crud[A] {

  val databaseName: String = provider.guessDatabaseName(collectionName)

  val name: String = provider.guessName(collectionName)

  val collection: MongoCollection[A] = provider.collection[A](collectionName)

  def addChangeObserver(observer: ChangeObserver[A]): ChangeObserver[A] = {
    coll.watch[A]().subscribe(observer)
    observer
  }

  def collectionStatus: Observable[CollectionStatus] =
    provider.runCommand(Map("collStats" -> collectionName)).map(document => CollectionStatus(document))

  /** @param sampleSize
    *   use sample size greater 0 for better performance on big collections
    * @return
    *   List of column names
    */
  def columnNames(sampleSize: Int = 0): List[String] = {
    val projectStage = project(Projections.computed("tempArray", equal("$objectToArray", "$$ROOT")))
    val unwindStage  = unwind("$tempArray")
    val groupStage   = group("_id", addToSet("keySet", "$tempArray.k"))
    val pipeline = {
      if (sampleSize > 0)
        List(sample(sampleSize), projectStage, unwindStage, groupStage)
      else
        List(projectStage, unwindStage, groupStage)
    }

    val aggregationResult: Document = Raw.findAggregated(pipeline).result()
    BsonConverter.fromBson(aggregationResult.get("keySet").head).asInstanceOf[List[String]]
  }

  protected def coll: MongoCollection[A] = collection

  // internal object for raw document access
  object Raw extends MongoDAO[Document](provider, collectionName)

  def importJsonFile(file: File): SingleObservable[BulkWriteResult] = {
    val docs = new ArrayBuffer[Document]()
    try if (file.exists) {
      val iterator = file.lineIterator(Charset.forName("UTF-8"))
      iterator.foreach(line => docs.+=(DocumentHelper.documentFromJsonString(line).get))
    }
    catch {
      case e: JsonParseException =>
        logger.error(e.getMessage, e)
    }
    Raw.bulkWriteMany(docs.toSeq)
  }

  override def toString: String = "%s:%s@%s, %s".format(databaseName, collectionName, provider.config, super.toString)
}
