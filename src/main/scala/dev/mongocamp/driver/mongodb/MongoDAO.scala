package dev.mongocamp.driver.mongodb

import better.files.File
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.bson.DocumentHelper
import dev.mongocamp.driver.mongodb.database._
import dev.mongocamp.driver.mongodb.operation.Crud
import dev.mongocamp.driver.mongodb.utils.FileUtils
import io.circe.Decoder
import java.net.URI
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.Date
import org.bson.json.JsonParseException
import org.bson.BsonDocument
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.bsonDocumentToDocument
import org.mongodb.scala.model.changestream.ChangeStreamDocument
import org.mongodb.scala.model.changestream.FullDocument
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections
import org.mongodb.scala.BulkWriteResult
import org.mongodb.scala.Document
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.Observable
import org.mongodb.scala.Observer
import org.mongodb.scala.SingleObservable
import org.mongodb.scala.Subscription
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

abstract class MongoDAO[A](provider: DatabaseProvider, collectionName: String)(implicit ct: ClassTag[A], decoder: Decoder[A]) extends Crud[A] {

  val databaseName: String = provider.guessDatabaseName(collectionName)

  val name: String = provider.guessName(collectionName)

  val collection: MongoCollection[Document] = provider.collection(collectionName)

  def topologyType(): Observable[TopologyType.TopologyType] = provider.topologyType()

  def addChangeObserver(observer: ChangeObserver[A]): ChangeObserver[A] = {
    coll
      .watch[Document]()
      .subscribe(new Observer[ChangeStreamDocument[Document]] {
        override def onSubscribe(s: Subscription): Unit = observer.onSubscribe(s)
        override def onError(e: Throwable): Unit        = observer.onError(e)
        override def onComplete(): Unit                 = observer.onComplete()
        override def onNext(event: ChangeStreamDocument[Document]): Unit =
          observer.onNext(event.asInstanceOf[ChangeStreamDocument[A]])
      })
    observer
  }

  def addChangeObserver(observer: ChangeObserver[A], fullDocument: FullDocument): ChangeObserver[A] = {
    addChangeObserver(observer, fullDocument, Seq.empty, None)
  }

  def addChangeObserver(observer: ChangeObserver[A], fullDocument: FullDocument, pipeline: Seq[Bson]): ChangeObserver[A] = {
    addChangeObserver(observer, fullDocument, pipeline, None)
  }

  def addChangeObserver(observer: ChangeObserver[A], fullDocument: FullDocument, pipeline: Seq[Bson], resumeAfter: Option[BsonDocument]): ChangeObserver[A] = {
    val baseStream  = if (pipeline.nonEmpty) coll.watch[Document](pipeline) else coll.watch[Document]()
    val withFullDoc = baseStream.fullDocument(fullDocument)
    val finalStream = resumeAfter.fold(withFullDoc)(
      token => withFullDoc.resumeAfter(token)
    )
    finalStream.subscribe(new Observer[ChangeStreamDocument[Document]] {
      override def onSubscribe(s: Subscription): Unit = observer.onSubscribe(s)
      override def onError(e: Throwable): Unit        = observer.onError(e)
      override def onComplete(): Unit                 = observer.onComplete()
      override def onNext(event: ChangeStreamDocument[Document]): Unit =
        observer.onNext(event.asInstanceOf[ChangeStreamDocument[A]])
    })
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
  def columnNames(sampleSize: Int, maxWait: Int): List[String] = {
    columnNames(sampleSize, Duration(maxWait, TimeUnit.SECONDS))
  }

  def columnNames(sampleSize: Int = 0, maxWait: Duration = DefaultMaxWaitDuration): List[String] = {
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

  def importJsonFile(url: URL): SingleObservable[BulkWriteResult] = {
    if (url.getProtocol == "file") {
      importJsonFile(File(url))
    }
    else {
      val file       = FileUtils.getFile(url)
      val observable = importJsonFile(file)
      file.delete()
      observable
    }
  }

  def importJsonFile(uri: URI): SingleObservable[BulkWriteResult] = importJsonFile(uri.toURL)

  override def toString: String = "%s:%s@%s, %s".format(databaseName, collectionName, provider.config, super.toString)
}
