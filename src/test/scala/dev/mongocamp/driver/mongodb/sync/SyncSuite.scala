package dev.mongocamp.driver.mongodb.sync

import dev.mongocamp.driver.mongodb.server.LocalServer
import dev.mongocamp.driver.mongodb.sync.TestSync._
import dev.mongocamp.driver.mongodb.test.UniversityDatabase
import munit.FunSuite

class SyncSuite extends FunSuite {

  val CountSmall  = 5
  val CountMedium = 500

  override def beforeAll(): Unit =
    UniversityDatabase.LocalTestServer = LocalServer.fromPath("unit.test.local.mongo.server")

  override def afterAll(): Unit = {
    TestSync.mongoSyncer.terminate()
    UniversityDatabase.LocalTestServer.shutdown()
  }

  test("Collection should be synced from source to target") {
    var result: MongoSyncResult = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
    assert(result.acknowleged)
    TestSync.insertIntoSource(CountMedium, TestCollectionSourceTargetName)
    result = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
    assert(result.acknowleged)
    assertEquals(result.synced, 500)
    assertEquals(result.countBefore, 0)
    assertEquals(result.countAfter, 500)
    assertEquals(TestSync.targetCount(TestCollectionSourceTargetName), 500L)
    result = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
    assert(result.acknowleged)
    assertEquals(result.countBefore, 500)
    assertEquals(result.synced, 0)

    TestSync.insertIntoSource(CountSmall, TestCollectionSourceTargetName)
    result = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName).head
    assert(result.acknowleged)
    assertEquals(result.countBefore, 500)
    assertEquals(result.countAfter, 505)
    assertEquals(result.synced, 5)
    assertEquals(TestSync.targetCount(TestCollectionSourceTargetName), 505L)

    TestSync.insertIntoTarget(CountSmall, TestCollectionSourceTargetName)
    assertEquals(TestSync.targetCount(TestCollectionSourceTargetName), 510L)
    val resultList = TestSync.mongoSyncer.sync(TestCollectionSourceTargetName)
    result = resultList.head
    assert(result.acknowleged)
    assertEquals(result.countBefore, 510)
    assertEquals(result.synced, 0)
    assertEquals(TestSync.sourceCount(TestCollectionSourceTargetName), 505L)
  }

  test("Collection should be synced two way") {
    var result: MongoSyncResult = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
    assert(result.acknowleged)
    TestSync.insertIntoSource(CountMedium, TestCollectionTwoWayName)
    result = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
    assert(result.acknowleged)
    assertEquals(result.synced, 500)
    assertEquals(result.countBefore, 0)
    assertEquals(result.countAfter, 500)
    assertEquals(TestSync.targetCount(TestCollectionTwoWayName), 500L)
    result = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
    assert(result.acknowleged)
    assertEquals(result.countBefore, 500)
    assertEquals(result.synced, 0)

    TestSync.insertIntoSource(CountSmall, TestCollectionTwoWayName)
    result = TestSync.mongoSyncer.sync(TestCollectionTwoWayName).head
    assert(result.acknowleged)
    assertEquals(result.countBefore, 500)
    assertEquals(result.countAfter, 505)
    assertEquals(result.synced, 5)
    assertEquals(TestSync.targetCount(TestCollectionTwoWayName), 505L)

    TestSync.insertIntoTarget(CountSmall, TestCollectionTwoWayName)
    assertEquals(TestSync.targetCount(TestCollectionTwoWayName), 510L)
    val resultList = TestSync.mongoSyncer.sync(TestCollectionTwoWayName)
    result = resultList.head
    assert(result.acknowleged)
    assertEquals(result.countBefore, 510)
    assertEquals(result.synced, 0)
    assertEquals(TestSync.sourceCount(TestCollectionTwoWayName), 510L)
  }
}
