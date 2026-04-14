package dev.mongocamp.driver.mongodb.collection

import com.mongodb.MongoCommandException
import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import com.mongodb.client.model.TimeSeriesGranularity

class CollectionManagementSuite extends munit.FunSuite with LazyLogging {

  private val CappedCollName      = "test_capped_col"
  private val TimeSeriesCollName  = "test_timeseries_col"

  override def beforeAll(): Unit = {
    // Clean up from previous runs
    Seq(CappedCollName, TimeSeriesCollName).foreach { name =>
      try provider.dao(name).drop().result()
      catch { case _: Exception => () }
    }
  }

  override def afterAll(): Unit = {
    Seq(CappedCollName, TimeSeriesCollName).foreach { name =>
      try provider.dao(name).drop().result()
      catch { case _: Exception => () }
    }
  }

  test("createCappedCollection creates a capped collection") {
    provider.createCappedCollection(
      collectionName = CappedCollName,
      maxSizeBytes   = 1024 * 1024 // 1 MB
    ).result()

    val info = provider.collectionInfos().find(_.name == CappedCollName)
    assert(info.isDefined, s"Collection '$CappedCollName' should exist after creation")
    assert(info.get.isCapped, s"Collection '$CappedCollName' should be reported as capped")
  }

  test("createCappedCollection respects maxDocuments option") {
    // Drop and recreate with maxDocuments
    try provider.dao(CappedCollName).drop().result()
    catch { case _: Exception => () }

    provider.createCappedCollection(
      collectionName = CappedCollName,
      maxSizeBytes   = 1024 * 1024,
      maxDocuments   = Some(100L)
    ).result()

    val info = provider.collectionInfos().find(_.name == CappedCollName)
    assert(info.isDefined)
    assert(info.get.isCapped)
    // The max documents value is stored in options.max
    val maxDocs = info.get.map
      .get("options")
      .flatMap { case m: Map[_, _] => m.asInstanceOf[Map[String, Any]].get("max") }
    assert(maxDocs.isDefined, "maxDocuments should be stored in collection options")
  }

  test("CollectionInfo.isCapped returns false for a regular collection") {
    val regularCollName = "test_regular_col"
    try provider.dao(regularCollName).drop().result()
    catch { case _: Exception => () }

    // Regular collections are created implicitly on first insert
    provider.database().createCollection(regularCollName).result()
    val info = provider.collectionInfos().find(_.name == regularCollName)
    assert(info.isDefined)
    assert(!info.get.isCapped, "A regular collection should not be capped")

    try provider.dao(regularCollName).drop().result()
    catch { case _: Exception => () }
  }

  // ---- time-series collection ----

  test("createTimeSeriesCollection creates a time-series collection") {
    try {
      provider.createTimeSeriesCollection(
        collectionName = TimeSeriesCollName,
        timeField      = "timestamp",
        metaField      = Some("sensorId"),
        granularity    = Some(TimeSeriesGranularity.SECONDS)
      ).result()

      val info = provider.collectionInfos().find(_.name == TimeSeriesCollName)
      assert(info.isDefined, s"Collection '$TimeSeriesCollName' should exist after creation")
      assert(info.get.isTimeSeries, s"Collection '$TimeSeriesCollName' should be reported as time-series")
    }
    catch {
      case e: MongoCommandException =>
        // Time-series requires MongoDB 5.0+; skip on older versions or embedded server
        logger.warn(s"Skipping time-series test (requires MongoDB 5.0+): ${e.getMessage}")
    }
  }

  test("CollectionInfo.isTimeSeries returns false for a regular collection") {
    val regularCollName = "test_regular_col2"
    try provider.dao(regularCollName).drop().result()
    catch { case _: Exception => () }

    provider.database().createCollection(regularCollName).result()
    val info = provider.collectionInfos().find(_.name == regularCollName)
    assert(info.isDefined)
    assert(!info.get.isTimeSeries, "A regular collection should not be time-series")

    try provider.dao(regularCollName).drop().result()
    catch { case _: Exception => () }
  }

}
