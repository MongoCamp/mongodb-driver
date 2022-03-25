package dev.mongocamp.driver.mongodb.database

import better.files.{File, Resource}
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.Document

class DatabaseProviderSpec extends PersonSpecification {

  "Database provider" should {

    "must evaluate databaseNames" in {
      val names = provider.databaseNames
      names must contain("mongocamp-unit-test")
    }

    "must evaluate collectionNames" in {
      val names = provider.collectionNames()
      names must contain("people")
    }

    "must evaluate dao by name" in {
      val dao         = provider.dao("people")
      val count: Long = dao.count().result()
      count mustEqual 200
    }

    "must evaluate dao by name in different database" in {
      val dao          = provider.dao("mongocamp-unit-test-2:people")
      val databaseName = dao.databaseName
      databaseName mustEqual "mongocamp-unit-test-2"
      dao.name mustEqual "people"
      provider.dropDatabase(databaseName).result()

      dao.importJsonFile(File(Resource.getUrl("json/people.json"))).result()
      val count: Long = dao.count().result()
      count mustEqual 200
    }

    "must evaluate buildInfo" in {

      val result: Document = provider.runCommand(Map("buildInfo" -> 1)).result()

      result.getDouble("ok") mustEqual 1.0
    }

    "must evaluate collection status" in {
      val status: Option[CollectionStatus] = provider.collectionStatus("people").resultOption()
      status must beSome()
      status.get.ns mustEqual "mongocamp-unit-test.people"
    }

    "must add ChangeObserver" in {
      val observer = ChangeObserver(consumeDatabaseChanges)
      // todo enable change log at test machine
      provider.addChangeObserver(observer)
      true must beTrue
    }

  }
}
