package dev.mongocamp.driver.mongodb.pagination

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.MongoImplicits
import dev.mongocamp.driver.mongodb.Aggregate._
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.{filter, sort}
import org.mongodb.scala.model.Filters.{and, equal}
class PaginationIterationSpec extends PersonSpecification with MongoImplicits with LazyLogging{

  "Pagination Iteration" should {

    "support with Filter" in {
      // #region filter-foreach
      val paginationFemale = MongoPaginatedFilter(PersonDAO, Map("gender" -> "female"), sortByKey("name"))

      val pageFemale = paginationFemale.paginate(1, 10)

      pageFemale.paginationInfo.allCount mustEqual 98

      var i = 0

      MongoPagination.foreach(paginationFemale, 5) { person =>
        {
          logger.trace(person.toString)
          i = i + 1
        }
      }
      i mustEqual 98
      // #endregion filter-foreach

    }

    "support with aggregation" in {
      // #region aggregation-foreach
      val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

      val sortStage: Bson = sort(sortByKey("age"))

      val pipeline = List(filterStage, sortStage)

      val pagination = MongoPaginatedAggregation(PersonDAO, pipeline, allowDiskUse = true)

      val page = pagination.paginate(1, 10)

      page.paginationInfo.allCount mustEqual 98
      var i = 0
      MongoPagination.foreach(pagination, 5) { element =>
        {
          logger.trace(element.toJson())
          i = i + 1
        }
      }
      i mustEqual 98
      // #endregion aggregation-foreach

    }

  }

}
