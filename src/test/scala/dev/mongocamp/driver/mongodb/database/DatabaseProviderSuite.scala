package dev.mongocamp.driver.mongodb.database

import better.files.{ File, Resource }
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.Document

class DatabaseProviderSuite extends BasePersonSuite {

  test("must evaluate databaseNames") {
    val names = provider.databaseNames
    assert(names.contains("mongocamp-unit-test"))
  }

  test("must evaluate collectionNames") {
    val names = provider.collectionNames()
    assert(names.contains("people"))
  }

  test("must evaluate mongo-dao by name") {
    val dao         = provider.dao("people")
    val count: Long = dao.count().result()
    assertEquals(count, 200L)
  }

  test("must evaluate mongo-dao by name in different database") {
    val dao          = provider.dao("mongocamp-unit-test-2:people")
    val databaseName = dao.databaseName
    assertEquals(databaseName, "mongocamp-unit-test-2")
    assertEquals(dao.name, "people")
    provider.dropDatabase(databaseName).result()

    dao.importJsonFile(File(Resource.getUrl("json/people.json"))).result()
    val count: Long = dao.count().result()
    assertEquals(count, 200L)
  }

  test("must evaluate buildInfo") {
    val result: Document = provider.runCommand(Map("buildInfo" -> 1)).result()
    val double : Double = result.getDouble("ok")
    assertEquals(double, 1.0)
  }

  test("must evaluate collection status") {
    val status: Option[CollectionStatus] = provider.collectionStatus("people").resultOption()
    assertEquals(status.isDefined, true)
    assertEquals(status.get.ns, "mongocamp-unit-test.people")
  }

  test("must add ChangeObserver") {
    val observer = ChangeObserver(consumeDatabaseChanges)
    provider.addChangeObserver(observer)
  }

}
