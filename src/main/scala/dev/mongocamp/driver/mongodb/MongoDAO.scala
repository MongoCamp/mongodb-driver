package dev.mongocamp.driver.mongodb

import better.files.File
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.bson.DocumentHelper
import dev.mongocamp.driver.mongodb.database.ChangeObserver
import dev.mongocamp.driver.mongodb.database.CollectionStatus
import dev.mongocamp.driver.mongodb.database.CompactResult
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.json._
import dev.mongocamp.driver.mongodb.operation.Crud
import io.circe.Decoder
import java.nio.charset.Charset
import java.util.Date
import org.bson.json.JsonParseException
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections
import org.mongodb.scala.BulkWriteResult
import org.mongodb.scala.Document
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.Observable
import org.mongodb.scala.SingleObservable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/** Created by tom on 20.01.17.
  */
abstract class MongoDAO[A](provider: DatabaseProvider, collectionName: String)(implicit ct: ClassTag[A], decoder: Decoder[A]) extends Crud[A] {

  val databaseName: String = provider.guessDatabaseName(collectionName)

  val name: String = provider.guessName(collectionName)

  val collection: MongoCollection[Document] = provider.collection(collectionName)

  def addChangeObserver(observer: ChangeObserver[A]): ChangeObserver[A] = {
    coll.watch[A]().subscribe(observer)
    observer
  }

  def collectionStatus: Observable[CollectionStatus] = {
    provider
      .runCommand(Map("collStats" -> collectionName))
      .map(
        document => CollectionStatus(document)
      )
  }

  def compact: Observable[Option[CompactResult]] = {
    val startDate = new Date()
    provider
      .runCommand(Map("compact" -> collectionName))
      .map(
        document => CompactResult(s"$databaseName${DatabaseProvider.CollectionSeparator}$collectionName", document, startDate)
      )
  }

  /** @param sampleSize
    *   use sample size greater 0 for better performance on big collections
    * @return
    *   List of column names
    */
  def columnNames(sampleSize: Int = 0, maxWait: Int = DefaultMaxWait): List[String] = {
    val projectStage = project(Projections.computed("tempArray", equal("$objectToArray", "$$ROOT")))
    val unwindStage  = unwind("$tempArray")
    val groupStage   = group("_id", addToSet("keySet", "$tempArray.k"))
    val pipeline = {
      if (sampleSize > 0) {
        List(sample(sampleSize), projectStage, unwindStage, groupStage)
      }
      else {
        List(projectStage, unwindStage, groupStage)
      }
    }

    val aggregationResult: Document = Raw.findAggregated(pipeline).result(maxWait)
    BsonConverter.fromBson(aggregationResult.get("keySet").head).asInstanceOf[List[String]]
  }

  protected def coll: MongoCollection[Document] = collection

  // internal object for raw document access
  object Raw extends MongoDAO[Document](provider, collectionName)

  def importJsonFile(file: File): SingleObservable[BulkWriteResult] = {
    val docs = new ArrayBuffer[Document]()
    try {
      if (file.exists) {
        val iterator = file.lineIterator(Charset.forName("UTF-8"))
        iterator.foreach(
          line => docs.+=(DocumentHelper.documentFromJsonString(line).get)
        )
      }
    }
    catch {
      case e: JsonParseException =>
        logger.error(e.getMessage, e)
    }
    Raw.bulkWriteMany(docs.toSeq)
  }

  override def toString: String = "%s:%s@%s, %s".format(databaseName, collectionName, provider.config, super.toString)
}
