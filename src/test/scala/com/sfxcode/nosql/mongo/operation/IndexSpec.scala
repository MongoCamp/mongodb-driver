package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification
import com.sfxcode.nosql.mongo.database.MongoIndex
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class IndexSpec extends PersonSpecification {

  "Base Index Operations" should {

    "return an index list" in {

      val list = PersonDAO.indexList
      list must haveSize(1)

      val mongoIndex: MongoIndex = list.head
      mongoIndex.name mustEqual "_id_"
      mongoIndex.key mustEqual "_id"
      mongoIndex.ascending mustEqual 1
      mongoIndex.namespace mustEqual "simple-mongo-unit-test.people"
      mongoIndex.version mustEqual 2
      mongoIndex.keys must haveSize(1)
      mongoIndex.keys.head._1 mustEqual "_id"
      mongoIndex.keys.head._2 mustEqual 1

    }

    "create / drop indexes for key" in {

      var createIndexResult: String = PersonDAO.createIndexForField("name")

      createIndexResult mustEqual "name_1"

      PersonDAO.indexList must haveSize(2)

      val dropIndexResult: Void = PersonDAO.dropIndexForName(createIndexResult)

      PersonDAO.indexList must haveSize(1)
    }

    "evaluate has index" in {

      PersonDAO.hasIndexForField("_id") must beTrue

      PersonDAO.hasIndexForField("unknown") must beFalse
    }

    "create descending index for key" in {

      var createIndexResult: String = PersonDAO.createIndexForFieldWithName("name", sortAscending = false, "myIndex")

      createIndexResult mustEqual "myIndex"

      PersonDAO.indexList must haveSize(2)

      PersonDAO.dropIndexForName(createIndexResult).result()

      PersonDAO.indexList must haveSize(1)
    }

    "create unique index for key" in {

      var createIndexResult: String =
        PersonDAO.createUniqueIndexForField("id", sortAscending = false, Some("myUniqueIndex"))

      createIndexResult mustEqual "myUniqueIndex"

      PersonDAO.indexList must haveSize(2)

      PersonDAO.dropIndexForName(createIndexResult).result()

      PersonDAO.indexList must haveSize(1)
    }

    "create hashed index for key" in {

      var createIndexResult: String = PersonDAO.createHashedIndexForField("email")

      createIndexResult mustEqual "email_hashed"

      PersonDAO.indexList must haveSize(2)

      PersonDAO.dropIndexForName(createIndexResult).result()

      PersonDAO.indexList must haveSize(1)
    }

  }

}
