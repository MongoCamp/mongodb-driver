package dev.mongocamp.driver.mongodb.pagination

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import dev.mongocamp.driver.mongodb.Aggregate._
import dev.mongocamp.driver.MongoImplicits
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.sort
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal
import scala.collection.mutable.ArrayBuffer

class PaginationIterationSuite extends BasePersonSuite with MongoImplicits with LazyLogging {

  test("support with Filter") {
    val paginationFemale = MongoPaginatedFilter(PersonDAO, Map("gender" -> "female"), sortByKey("name"))

    val pageFemale = paginationFemale.paginate(1, 10)

    assertEquals(pageFemale.paginationInfo.allCount, 98L)

    var i           = 0
    val personArray = new ArrayBuffer[Person]()
    // #region foreach-with-rows
    paginationFemale.foreach(5) {
      person =>
        {
          logger.trace(person.toString)
          personArray += person
          i = i + 1
        }
    }
    assertEquals(i, 98)
    // #endregion foreach-with-rows
  }

  test("support with aggregation") {
    val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

    val sortStage: Bson = sort(sortByKey("age"))

    val pipeline = List(filterStage, sortStage)

    val pagination = MongoPaginatedAggregation(PersonDAO, pipeline, allowDiskUse = true)

    val page = pagination.paginate(1, 10)

    assertEquals(page.paginationInfo.allCount, 98L)

    // #region foreach-default-rows
    var i = 0
    pagination.foreach {
      element =>
        {
          logger.trace(element.toJson())
          i = i + 1
        }
    }
    assertEquals(i, 98)
    // #endregion foreach-default-rows
  }

}
