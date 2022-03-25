package dev.mongocamp.driver.mongodb.sync

import dev.mongocamp.driver.mongodb.server.LocalServer
import dev.mongocamp.driver.mongodb.sync.TestSync._
import dev.mongocamp.driver.mongodb.test.UniversityDatabase
import org.specs2.mutable.Specification
import org.specs2.specification.{AfterAll, BeforeAll}

class SyncSpec extends Specification with BeforeAll with AfterAll {

  val CountSmall  = 5
  val CountMedium = 500

  sequential

  override def beforeAll(): Unit =
    UniversityDatabase.LocalTestServer = LocalServer.fromPath("unit.test.local.mongo.server")

  override def afterAll(): Unit = {
    TestSync.mongoSyncer.terminate()
    UniversityDatabase.LocalTestServer.shutdown()
  }

  "Collection" should {

    "be synced from source to target" in {
      var result: MongoSyncResult = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
      result.acknowleged must beTrue
      TestSync.insertIntoSource(CountMedium, TestCollectionSourceTargetName)
      result = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
      result.acknowleged must beTrue
      result.synced mustEqual 500
      result.countBefore mustEqual 0
      result.countAfter mustEqual 500
      TestSync.targetCount(TestCollectionSourceTargetName) mustEqual 500
      result = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
      result.acknowleged must beTrue
      result.countBefore mustEqual 500
      result.synced mustEqual 0

      TestSync.insertIntoSource(CountSmall, TestCollectionSourceTargetName)
      result = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
      result.acknowleged must beTrue
      result.countBefore mustEqual 500
      result.countAfter mustEqual 505
      result.synced mustEqual 5
      TestSync.targetCount(TestCollectionSourceTargetName) mustEqual 505

      TestSync.insertIntoTarget(CountSmall, TestCollectionSourceTargetName)
      TestSync.targetCount(TestCollectionSourceTargetName) mustEqual 510
      val resultList = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName)
      result = resultList.head
      result.acknowleged must beTrue
      result.countBefore mustEqual 510
      result.synced mustEqual 0
      TestSync.sourceCount(TestCollectionSourceTargetName) mustEqual 505
    }

    "be synced two way" in {
      var result: MongoSyncResult = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
      result.acknowleged must beTrue
      TestSync.insertIntoSource(CountMedium, TestCollectionTwoWayName)
      result = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
      result.acknowleged must beTrue
      result.synced mustEqual 500
      result.countBefore mustEqual 0
      result.countAfter mustEqual 500
      TestSync.targetCount(TestCollectionTwoWayName) mustEqual 500
      result = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
      result.acknowleged must beTrue
      result.countBefore mustEqual 500
      result.synced mustEqual 0

      TestSync.insertIntoSource(CountSmall, TestCollectionTwoWayName)
      result = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
      result.acknowleged must beTrue
      result.countBefore mustEqual 500
      result.countAfter mustEqual 505
      result.synced mustEqual 5
      TestSync.targetCount(TestCollectionTwoWayName) mustEqual 505

      TestSync.insertIntoTarget(CountSmall, TestCollectionTwoWayName)
      TestSync.targetCount(TestCollectionTwoWayName) mustEqual 510
      val resultList = TestSync.mongoSyncer.sync(TestCollectionTwoWayName)
      result = resultList.head
      result.acknowleged must beTrue
      result.countBefore mustEqual 510
      result.synced mustEqual 0
      TestSync.sourceCount(TestCollectionTwoWayName) mustEqual 510
    }

  }
}
