package dev.mongocamp.driver.mongodb

import better.files.File
import better.files.Resource
import dev.mongocamp.driver.mongodb.database.CompactResult
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.test.TestDatabase.BookDAO
import java.text.SimpleDateFormat
import java.util.Date
import munit.FunSuite

class CompactSuite extends FunSuite {
  val DateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val From: Date = DateFormat.parse("2000-01-01")

  override def beforeAll(): Unit = {
    super.beforeAll()
    BookDAO.drop().result()
    BookDAO.importJsonFile(File(Resource.getUrl("json/books.json"))).result()
    val stats = BookDAO.collectionStatus.result()
    assertEquals(stats.count, 431)
  }

  test("compact single collection") {
    val count: Option[CompactResult] = BookDAO.compact.result()
    assertEquals(count.isDefined, true)
    assertEquals(count.get.bytesFreed >= 0L, true)
  }

  test("compact complete database") {
    val count: List[CompactResult] = TestDatabase.provider.compactDatabase()
    assertEquals(count.nonEmpty, true)
    assertEquals(count.head.bytesFreed >= 0L, true)
  }

  test("compact all databases in scope") {
    val count: List[CompactResult] = TestDatabase.provider.compact()
    assertEquals(count.nonEmpty, true)
    assertEquals(count.head.bytesFreed >= 0L, true)
  }
}
