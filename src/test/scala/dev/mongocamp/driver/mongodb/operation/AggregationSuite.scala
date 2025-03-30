package dev.mongocamp.driver.mongodb.operation

// #region agg_imports
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import dev.mongocamp.driver.mongodb.Aggregate._
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Aggregates.filter
import org.mongodb.scala.model.Aggregates.group
import org.mongodb.scala.model.Aggregates.sort
import org.mongodb.scala.model.Filters.and
import org.mongodb.scala.model.Filters.equal

class AggregationSuite extends BasePersonSuite {

  // #region agg_stages
  val filterStage: Bson = filter(and(equal("gender", "female"), notNullFilter("balance")))

  val groupStage: Bson = group(Map("age" -> "$age"), sumField("balance"), firstField("age"))

  val sortStage: Bson = sort(sortByKey("age"))
  // #endregion agg_stages

  test("support aggregation filter") {

    val pipeline = List(filterStage, sortStage)

    val aggregated = PersonDAO.findAggregated(pipeline).resultList()

    assertEquals(aggregated.size, 98)

  }

  test("support aggregation filter and group") {
    // #region agg_execute
    val pipeline = List(filterStage, groupStage, sortStage)

    val aggregated = PersonDAO.Raw.findAggregated(pipeline).resultList()
    // #endregion agg_execute

    assertEquals(aggregated.size, 21)

    // #region agg_convert
    val list: List[Map[String, Any]] = aggregated
    // #endregion agg_convert
    list.foreach(
      m => println(m("age").toString + " -> " + m("balance"))
    )

    assertEquals(list.head("age"), 20)
    assertEquals(list.head("balance"), 8333.0)
  }

}
