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
      val aggregator  = List(filterStage, projectStage)

      val list = BookDAO.Raw.findAggregated(aggregator).resultList()
      list must haveSize(8)
    }

    "support filter in" in {

      val groupStage: Bson =
        group("$categories", Field.sumField("pageCount"), Field.minField("pageCount"), Field.maxField("pageCount"))

      val aggregator = List(groupStage)

      val list = BookDAO.Raw.findAggregated(aggregator).resultList().map(doc => doc.asPlainMap)
      list must haveSize(58)
    }
  }
}
