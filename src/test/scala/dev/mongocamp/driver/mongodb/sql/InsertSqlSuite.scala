package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase

class InsertSqlSuite extends BasePersonSuite {

  test("insert") {
    val dao = TestDatabase.provider.dao("table_name")
    dao.drop().resultList()
    val queryConverter = MongoSqlQueryHolder("INSERT INTO table_name (column1, column2, column3) VALUES ('value1', 123, '2022-01-01T00:00:00.000Z');")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertNotEquals(selectResponse.head.get("insertedIds"), null)
    val documents = dao.find().resultList()
    assertEquals(documents.size, 1)
    assertEquals(documents.head.getString("column1"), "value1")
    assertEquals(documents.head.getLong("column2").toLong, 123L)
  }

  test("insert 2 rows") {
    val dao = TestDatabase.provider.dao("table_name")
    dao.drop().resultList()
    val queryConverter = MongoSqlQueryHolder(
      "INSERT INTO table_name (column1, column2, column3) VALUES ('value1', 123, '2022-01-01T00:00:00.000Z'), ('value2', 456, '2022-02-01T00:00:00.000Z');"
    )
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    assert(selectResponse.head.getBoolean("wasAcknowledged"))
    assertNotEquals(selectResponse.head.get("insertedIds"), null)
    val documents = dao.find().resultList()
    assertEquals(documents.size, 2)
    assertEquals(documents.head.getString("column1"), "value1")
    assertEquals(documents.head.getLong("column2").toLong, 123L)
  }

  test("insert not named") {
    var errorCaught = false
    try {
      val dao = TestDatabase.provider.dao("table_name")
      dao.drop().resultList()
      MongoSqlQueryHolder("INSERT INTO table_name VALUES ('value1', 123, '2022-01-01T00:00:00.000Z');")
    }
    catch {
      case e: Exception =>
        assertEquals(e.getMessage, "column names must be specified")
        errorCaught = true
    }
    assertEquals(errorCaught, true)
  }

}
