package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.database.MongoIndex
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

class IndexSpec extends Specification with BeforeAll {

  sequential

  "Base Index Operations" should {

    "return an index list" in {

      val list = PersonDAO.indexList
      list must haveSize(1)

      val mongoIndex: MongoIndex = list.head
      mongoIndex.name must be equalTo "_id_"
      mongoIndex.key must be equalTo "_id"
      mongoIndex.ascending must be equalTo 1
      mongoIndex.namespace must contain("person")
      mongoIndex.version must be equalTo 2
      mongoIndex.keys must haveSize(1)
      mongoIndex.keys.head._1 must be equalTo "_id"
      mongoIndex.keys.head._2 must be equalTo 1

    }

    "create / drop indexes for key" in {

      var createIndexResult: String = PersonDAO.createIndexForField("name")

      createIndexResult must be equalTo "name_1"

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

      createIndexResult must be equalTo "myIndex"

      PersonDAO.indexList must haveSize(2)

      PersonDAO.dropIndexForName(createIndexResult).result()

      PersonDAO.indexList must haveSize(1)
    }

    "create unique index for key" in {

      var createIndexResult: String =
        PersonDAO.createUniqueIndexForField("id", sortAscending = false, Some("myUniqueIndex"))

      createIndexResult must be equalTo "myUniqueIndex"

      PersonDAO.indexList must haveSize(2)

      PersonDAO.dropIndexForName(createIndexResult).result()

      PersonDAO.indexList must haveSize(1)
    }

    "create hashed index for key" in {

      var createIndexResult: String = PersonDAO.createHashedIndexForField("email")

      createIndexResult must be equalTo "email_hashed"

      PersonDAO.indexList must haveSize(2)

      PersonDAO.dropIndexForName(createIndexResult).result()

      PersonDAO.indexList must haveSize(1)
    }

  }

  override def beforeAll: Unit = BookDAO.drop().result()

}
