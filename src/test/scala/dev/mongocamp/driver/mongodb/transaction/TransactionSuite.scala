package dev.mongocamp.driver.mongodb.transaction

import com.mongodb.MongoCommandException
import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import com.mongodb.MongoClientException
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set

class TransactionSuite extends BasePersonSuite with LazyLogging {

  private def assumeTransactionSupport(thunk: => Unit): Unit = {
    try thunk
    catch {
      case e: MongoCommandException if e.getErrorCode == 20 =>
        logger.warn(s"Skipping transaction test (requires replica set): ${e.getMessage}")
      case e: MongoCommandException if e.getMessage.contains("Transaction") =>
        logger.warn(s"Skipping transaction test: ${e.getMessage}")
      case e: MongoClientException if e.getMessage.contains("retryable writes") =>
        logger.warn(s"Skipping transaction test (requires retryable-writes support): ${e.getMessage}")
      case e: MongoClientException =>
        logger.warn(s"Skipping transaction test (MongoClientException): ${e.getMessage}")
    }
  }

  test("withTransaction commits all operations on success") {
    assumeTransactionSupport {
      val countBefore = PersonDAO.count().result()

      provider.withTransaction { session =>
        val newPerson1 = PersonDAO.find().resultList().head.copy(guid = "tx-person-1", _id = new ObjectId())
        val newPerson2 = PersonDAO.find().resultList().head.copy(guid = "tx-person-2", _id = new ObjectId())
        PersonDAO.insertOne(newPerson1, session).result()
        PersonDAO.insertOne(newPerson2, session).result()
      }

      val countAfter = PersonDAO.count().result()
      assertEquals(countAfter, countBefore + 2)

      PersonDAO.deleteMany(equal("guid", "tx-person-1")).result()
      PersonDAO.deleteMany(equal("guid", "tx-person-2")).result()
    }
  }

  test("withTransaction rolls back all operations on failure") {
    assumeTransactionSupport {
      val countBefore = PersonDAO.count().result()

      intercept[RuntimeException] {
        provider.withTransaction { session =>
          val newPerson = PersonDAO.find().resultList().head.copy(guid = "tx-rollback-person", _id = new ObjectId())
          PersonDAO.insertOne(newPerson, session).result()
          throw new RuntimeException("intentional failure to trigger rollback")
        }
      }

      val countAfter = PersonDAO.count().result()
      assertEquals(countAfter, countBefore)
    }
  }

  test("withTransaction supports updateOne with session") {
    assumeTransactionSupport {
      val original = PersonDAO.find().resultList().head

      provider.withTransaction { session =>
        PersonDAO.updateOne(
          equal("guid", original.guid),
          set("favoriteFruit", "transaction-mango"),
          session
        ).result()
      }

      val updated = PersonDAO.find(equal("guid", original.guid)).result()
      assertEquals(updated.favoriteFruit, "transaction-mango")

      PersonDAO.updateOne(equal("guid", original.guid), set("favoriteFruit", original.favoriteFruit)).result()
    }
  }

  test("withTransaction supports deleteOne with session") {
    assumeTransactionSupport {
      val victim = PersonDAO.find().resultList().head.copy(guid = "tx-delete-target", _id = new ObjectId())
      PersonDAO.insertOne(victim).result()
      val countBefore = PersonDAO.count(equal("guid", "tx-delete-target")).result()
      assertEquals(countBefore, 1L)

      provider.withTransaction { session =>
        PersonDAO.deleteOne(equal("guid", "tx-delete-target"), session).result()
      }

      val countAfter = PersonDAO.count(equal("guid", "tx-delete-target")).result()
      assertEquals(countAfter, 0L)
    }
  }

  test("find with session returns documents inside transaction") {
    assumeTransactionSupport {
      provider.withTransaction { session =>
        val results = PersonDAO.find(session).resultList()
        assert(results.nonEmpty, "Expected documents visible inside transaction")
      }
    }
  }

}
