package com.sfxcode.nosql.mongo.dao

import java.text.SimpleDateFormat
import java.util.Date

import better.files.{File, Resource}
import com.sfxcode.nosql.mongo.Filter._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.test.TestDatabase.BookDAO
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Projections
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll
import org.mongodb.scala.model.Updates._

class BookDAOSpec extends Specification with BeforeAll {
  val DateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val From       = DateFormat.parse("2000-01-01")

  override def beforeAll(): Unit = {
    BookDAO.drop().result()
    BookDAO.importJsonFile(File(Resource.getUrl("json/books.json"))).result()

    val stats = BookDAO.collectionStatus.result()
    stats.count mustEqual 431

  }

  "BookDAO" should {
    "support count" in {
      val count: Long = BookDAO.count().result()
      count mustEqual 431
    }
  }

  "BookDAO Aggregation" should {
    "support filter in" in {
      val projectStage = project(Projections.include("categories"))

      val categoryFilter = valueFilter("categories", "Programming")
      val dateFilter     = dateInRangeFilter("publishedDate", From, new Date())

      val filterStage = filter(and(categoryFilter, dateFilter))
      val pipeline    = List(filterStage, projectStage)

      val list = BookDAO.Raw.findAggregated(pipeline).resultList()
      list must haveSize(8)
    }

    "support filter in" in {

      val groupStage: Bson =
        group("$categories", Field.sumField("pageCount"), Field.minField("pageCount"), Field.maxField("pageCount"))

      val pipeline = List(groupStage)

      val list = BookDAO.Raw.findAggregated(pipeline).resultList().map(doc => doc.asPlainMap)
      list must haveSize(58)
    }

    "update one" in {
      BookDAO.updateOne(Map("_id" -> 10), set("title", "new title")).result()
      BookDAO.find("_id", 10).result().title mustEqual "new title"
    }
  }
}
