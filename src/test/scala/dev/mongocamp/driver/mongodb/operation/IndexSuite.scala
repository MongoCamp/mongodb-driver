package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.database.{ DatabaseProvider, MongoIndex }
import dev.mongocamp.driver.mongodb.test.TestDatabase._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration

class IndexSuite extends BasePersonSuite {

  test("create / drop indexes for key") {
    val createIndexResult: String = PersonDAO.createIndexForField("name").result()
    assertEquals(createIndexResult, "name_1")
    assertEquals(PersonDAO.indexList().size, 2)
    val index: MongoIndex = PersonDAO.indexForName("name_1").get
    assert(!index.expire)
    PersonDAO.dropIndexForName(createIndexResult).result()
    assertEquals(PersonDAO.indexList().size, 1)
  }

  test("evaluate has index") {
    assert(PersonDAO.hasIndexForField(DatabaseProvider.ObjectIdKey))
    assert(!PersonDAO.hasIndexForField("unknown"))
  }

  test("create descending index for key") {
    val createIndexResult: String = PersonDAO.createIndexForFieldWithName("name", sortAscending = false, "myIndex").result()
    assertEquals(createIndexResult, "myIndex")
    assertEquals(PersonDAO.indexList().size, 2)
    PersonDAO.indexForName("myIndex").get
    PersonDAO.dropIndexForName(createIndexResult).result()
    assertEquals(PersonDAO.indexList().size, 1)
  }

  test("create unique index for key") {
    val createIndexResult: String = PersonDAO.createUniqueIndexForField("id", sortAscending = false, Some("myUniqueIndex")).result()
    assertEquals(createIndexResult, "myUniqueIndex")
    assertEquals(PersonDAO.indexList().size, 2)
    PersonDAO.indexForName("myUniqueIndex").get
    PersonDAO.dropIndexForName(createIndexResult).result()
    assertEquals(PersonDAO.indexList().size, 1)
  }

  test("create text index for key") {
    val createIndexResult: String = PersonDAO.createTextIndexForField("email").result()
    assertEquals(createIndexResult, "email_text")
    assertEquals(PersonDAO.indexList().size, 2)
    PersonDAO.indexForName("email_text").get
    PersonDAO.dropIndexForName(createIndexResult).result()
    assertEquals(PersonDAO.indexList().size, 1)
  }

  test("create expiring index for key") {
    val createIndexResult: String = PersonDAO.createExpiringIndexForField("email", Duration(1, TimeUnit.SECONDS)).result()
    assertEquals(createIndexResult, "email_1")
    assertEquals(PersonDAO.indexList().size, 2)
    val index: MongoIndex = PersonDAO.indexForName("email_1").get
    assert(index.expire)
    PersonDAO.dropIndexForName(createIndexResult).result()
    assertEquals(PersonDAO.indexList().size, 1)
  }

  test("return an index list") {
    val list = PersonDAO.indexList()
    assertEquals(list.size, 1)
    val mongoIndex: MongoIndex = list.head
    assertEquals(mongoIndex.name, "_id_")
    assert(mongoIndex.fields.contains(DatabaseProvider.ObjectIdKey))
    assertEquals(mongoIndex.version, 2)
    assertEquals(mongoIndex.keys.size, 1)
    assertEquals(mongoIndex.keys.head._1, DatabaseProvider.ObjectIdKey)
    assertEquals(mongoIndex.keys.head._2, 1)
  }

}
