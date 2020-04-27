package com.sfxcode.nosql.mongo.database

import better.files.{File, Resource}
import com.mongodb.MongoBulkWriteException
import com.sfxcode.nosql.mongo.test.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification
import org.mongodb.scala.Document
import org.specs2.mutable.Specification

class DatabaseProviderSpec extends PersonSpecification {

  "Database provider" should {

    "must evaluate databaseNames" in {
      val names = provider.databaseNames
      names must contain("simple-mongo-unit-test")
    }

    "must evaluate collectionNames" in {
      val names = provider.collectionNames()
      names must contain("people")
    }

    "must evaluate dao by name" in {
      val dao         = provider.dao("people")
      val count: Long = dao.count()
      count mustEqual 200
    }

    "must evaluate dao by name in different database" in {
      val dao          = provider.dao("simple-mongo-unit-test-2:people")
      val databaseName = dao.databaseName
      databaseName mustEqual "simple-mongo-unit-test-2"
      dao.name mustEqual "people"
      provider.dropDatabase(databaseName).result()

      dao.importJsonFile(File(Resource.getUrl("json/people.json"))).result()
      val count: Long = dao.count()
      count mustEqual 200
    }

    "must evaluate buildInfo" in {

      val result: Document = provider.runCommand(Map("buildInfo" -> 1)).result()

      result.getDouble("ok") mustEqual 1.0
    }

    "must evaluate collection status" in {
      val status: Option[CollectionStatus] = provider.collectionStatus("people").resultOption()
      status must beSome()
      status.get.ns mustEqual "simple-mongo-unit-test.people"
    }

    "must add ChangeObserver" in {
      val observer = ChangeObserver(consumeDatabaseChanges)
      // todo enable change log at test machine
      provider.addChangeObserver(observer)
      true must beTrue
    }

  }
}
