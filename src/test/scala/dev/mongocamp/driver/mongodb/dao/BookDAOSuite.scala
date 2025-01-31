package dev.mongocamp.driver.mongodb.dao

import better.files.{ File, Resource }
import dev.mongocamp.driver.mongodb.Filter._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.test.TestDatabase.BookDAO
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{ filter, group, project }
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Projections
import org.mongodb.scala.model.Updates._

import java.text.SimpleDateFormat
import java.util.Date

class BookDAOSuite extends munit.FunSuite {
  val DateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val From: Date = DateFormat.parse("2000-01-01")

  override def beforeAll(): Unit = {
    BookDAO.drop().result()
    BookDAO.importJsonFile(File(Resource.getUrl("json/books.json"))).result()

    val stats = BookDAO.collectionStatus.result()
    assertEquals(stats.count, 431)
  }

  test("BookDAO should support count") {
    val count: Long = BookDAO.count().result()
    assertEquals(count, 431L)
  }

  test("BookDAO should support columnNames") {
    val columnNames = BookDAO.columnNames()
    assertEquals(columnNames.size, 11)
  }

  test("BookDAO Aggregation should support filter in") {
    val projectStage = project(Projections.include("categories"))

    val categoryFilter = valueFilter("categories", "Programming")
    val dateFilter     = dateInRangeFilter("publishedDate", From, new Date())

    val filterStage = filter(and(categoryFilter, dateFilter))
    val pipeline    = List(filterStage, projectStage)

    val list = BookDAO.Raw.findAggregated(pipeline).resultList()
    assertEquals(list.size, 8)
  }

  test("BookDAO Aggregation should support group by categories") {
    val groupStage: Bson =
      group("$categories", Field.sumField("pageCount"), Field.minField("pageCount"), Field.maxField("pageCount"))

    val pipeline = List(groupStage)

    val list = BookDAO.Raw.findAggregated(pipeline).resultList().map(doc => doc.asPlainMap)
    assertEquals(list.size, 58)
  }

  test("BookDAO should update one") {
    BookDAO.updateOne(Map(DatabaseProvider.ObjectIdKey -> 10), set("title", "new title")).result()
    assertEquals(BookDAO.find(DatabaseProvider.ObjectIdKey, 10).result().title, "new title")
  }
}