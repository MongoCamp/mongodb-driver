package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase
import org.mongodb.scala.Document

class InsertSqlSpec extends PersonSpecification {

  "MongoSqlQueryHolder" should {

    "insert" in {
      val dao = TestDatabase.provider.dao("table_name")
      dao.drop().resultList()
      val queryConverter = MongoSqlQueryHolder("INSERT INTO table_name (column1, column2, column3) VALUES ('value1', 123, '2022-01-01T00:00:00.000Z');")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.get("insertedIds") mustNotEqual null
      val documents = dao.find().resultList()
      documents.size mustEqual 1
      documents.head.getString("column1") mustEqual "value1"
      documents.head.getLong("column2") mustEqual 123
    }

    "insert 2 rows" in {
      val dao = TestDatabase.provider.dao("table_name")
      dao.drop().resultList()
      val queryConverter = MongoSqlQueryHolder(
        "INSERT INTO table_name (column1, column2, column3) VALUES ('value1', 123, '2022-01-01T00:00:00.000Z'), ('value2', 456, '2022-02-01T00:00:00.000Z');"
      )
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1
      selectResponse.head.getBoolean("wasAcknowledged") mustEqual true
      selectResponse.head.get("insertedIds") mustNotEqual null
      val documents = dao.find().resultList()
      documents.size mustEqual 2
      documents.head.getString("column1") mustEqual "value1"
      documents.head.getLong("column2") mustEqual 123
    }

    "insert not named" in {
      var errorCaught = false
      try {
        val dao = TestDatabase.provider.dao("table_name")
        dao.drop().resultList()
        MongoSqlQueryHolder("INSERT INTO table_name VALUES ('value1', 123, '2022-01-01T00:00:00.000Z');")
      }
      catch {
        case e: Exception =>
          e.getMessage mustEqual "column names must be specified"
          errorCaught = true
      }
      errorCaught mustEqual true
    }

  }
}
