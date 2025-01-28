package dev.mongocamp.driver.mongodb

import better.files.{ File, Resource }
import dev.mongocamp.driver.mongodb.database.CompactResult
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.test.TestDatabase.BookDAO
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

import java.text.SimpleDateFormat
import java.util.Date

class CompactSpec extends Specification with BeforeAll {
  val DateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val From: Date = DateFormat.parse("2000-01-01")

  override def beforeAll(): Unit = {
    BookDAO.drop().result()
    BookDAO.importJsonFile(File(Resource.getUrl("json/books.json"))).result()
    val stats = BookDAO.collectionStatus.result()
    stats.count mustEqual 431
  }

  "CompactSpec" should {
    "compact single collection" in {
      val count: Option[CompactResult] = BookDAO.compact.result()
      count.isDefined must beTrue
      count.get.bytesFreed must beGreaterThanOrEqualTo(0L)
    }
    "compact complete database" in {
      val count: List[CompactResult] = TestDatabase.provider.compactDatabase()
      count.size must beGreaterThanOrEqualTo(1)
      count.head.bytesFreed must beGreaterThanOrEqualTo(0L)
    }
    "compact all databases in scope" in {
      val count: List[CompactResult] = TestDatabase.provider.compact()
      count.size must beGreaterThanOrEqualTo(1)
      count.head.bytesFreed must beGreaterThanOrEqualTo(0L)
    }
  }

}
