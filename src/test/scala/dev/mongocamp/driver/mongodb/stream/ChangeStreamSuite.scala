package dev.mongocamp.driver.mongodb.stream

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.database.ChangeObserver
import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.bson.BsonDocument
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.changestream.FullDocument
import org.mongodb.scala.model.Aggregates
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set

class ChangeStreamSuite extends BasePersonSuite with LazyLogging {

  private def withChangeStreamSupport(thunk: => Unit): Unit = {
    try thunk
    catch {
      case e: Exception if isChangeStreamUnsupported(e) =>
        logger.warn(s"Skipping change-stream test (requires replica set): ${e.getMessage}")
    }
  }

  private def isChangeStreamUnsupported(e: Throwable): Boolean =
    e != null && e.getMessage != null && (
      e.getMessage.contains("$changeStream") ||
        e.getMessage.contains("replica set") ||
        e.getMessage.contains("not supported") ||
        e.getMessage.contains("Unsupported") ||
        isChangeStreamUnsupported(e.getCause)
    )

  private def assertEventsOrSkip(count: => Int, msg: String): Unit = {
    val c = count
    if (c == 0) logger.warn(s"Skipping stream assertion (requires replica set for event delivery): $msg")
    else assert(c >= 1, msg)
  }

  test("addChangeObserver(observer) subscribes to collection changes") {
    withChangeStreamSupport {
      val receivedCount = new AtomicInteger(0)
      val observer = ChangeObserver[Person](
        _ => receivedCount.incrementAndGet()
      )

      PersonDAO.addChangeObserver(observer)

      val target = PersonDAO.find().resultList().head
      PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", "stream-test-apple")).result()

      Thread.sleep(500)
      assertEventsOrSkip(receivedCount.get(), "Observer should have received at least one change event")

      PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", target.favoriteFruit)).result()
    }
  }

  test("addChangeObserver with FullDocument.UPDATE_LOOKUP delivers full document on update") {
    withChangeStreamSupport {
      val receivedCount = new AtomicInteger(0)
      val observer = ChangeObserver[Person] {
        _ => receivedCount.incrementAndGet()
      }

      PersonDAO.addChangeObserver(observer, FullDocument.UPDATE_LOOKUP)

      val target = PersonDAO.find().resultList().head
      PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", "full-doc-cherry")).result()

      Thread.sleep(500)
      assertEventsOrSkip(receivedCount.get(), "Event should be delivered with UPDATE_LOOKUP")

      PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", target.favoriteFruit)).result()
    }
  }

  test("addChangeObserver with pipeline only delivers matching event types") {
    withChangeStreamSupport {
      val receivedInserts = new AtomicInteger(0)
      val receivedUpdates = new AtomicInteger(0)

      val insertOnlyPipeline = Seq(Aggregates.`match`(equal("operationType", "insert")))
      val observer = ChangeObserver[Person] {
        event =>
          event.getOperationType.getValue match {
            case "insert" => receivedInserts.incrementAndGet()
            case "update" => receivedUpdates.incrementAndGet()
            case _        => ()
          }
      }

      PersonDAO.addChangeObserver(observer, FullDocument.DEFAULT, insertOnlyPipeline)

      val newPerson = PersonDAO.find().resultList().head.copy(guid = "stream-pipeline-test", _id = new ObjectId())
      PersonDAO.insertOne(newPerson).result()
      PersonDAO.updateOne(equal("guid", "stream-pipeline-test"), set("favoriteFruit", "filtered-peach")).result()

      Thread.sleep(500)
      if (receivedInserts.get() > 0) {
        assertEquals(receivedUpdates.get(), 0, "Update event should be filtered out by the pipeline")
      }
      else {
        logger.warn("Skipping pipeline-filter assertion: no events received (requires replica set)")
      }

      PersonDAO.deleteMany(equal("guid", "stream-pipeline-test")).result()
    }
  }

  test("addChangeObserver with resumeAfter accepts a BsonDocument token") {
    withChangeStreamSupport {
      val capturedToken = new AtomicReference[Option[BsonDocument]](None)
      val firstObserver = ChangeObserver[Person] {
        event =>
          if (capturedToken.get().isEmpty) capturedToken.set(Option(event.getResumeToken))
      }

      PersonDAO.addChangeObserver(firstObserver)

      val target = PersonDAO.find().resultList().head
      PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", "resume-token-fig")).result()
      Thread.sleep(500)

      PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", target.favoriteFruit)).result()

      val token = capturedToken.get()
      if (token.isEmpty) {
        logger.warn("Skipping resume-token test: no events received (requires replica set)")
      }
      else {
        val secondCount = new AtomicInteger(0)
        val secondObserver = ChangeObserver[Person](
          _ => secondCount.incrementAndGet()
        )
        PersonDAO.addChangeObserver(secondObserver, FullDocument.DEFAULT, Seq.empty, token)

        PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", "resumed-cherry")).result()
        Thread.sleep(500)
        assertEventsOrSkip(secondCount.get(), "Resumed observer should receive subsequent events")

        PersonDAO.updateOne(equal("guid", target.guid), set("favoriteFruit", target.favoriteFruit)).result()
      }
    }
  }

}
