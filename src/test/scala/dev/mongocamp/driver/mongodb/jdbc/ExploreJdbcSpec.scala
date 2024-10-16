package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb.MongoDAO
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.model.{Grade, Score}
import dev.mongocamp.driver.mongodb.test.TestDatabase
import org.bson.types.ObjectId

import java.sql.{DriverManager, ResultSet, Types}
import java.util.Properties
import scala.collection.mutable.ArrayBuffer
import better.files.{File, Resource}
import dev.mongocamp.driver.mongodb.{GenericObservable, MongoDAO}
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.model.{Grade, Score}
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDAO
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import org.specs2.specification.{BeforeAll, BeforeEach}

class ExploreJdbcSpec extends BaseJdbcSpec {

  "Jdbc Connection" should {

    "get table names" in {
      val tableNames = connection.getMetaData.getTables("%", "mongocamp-unit-test", "", Array.empty)
      var tables           = 0
      var tablePersonFound = false
      while (tableNames.next()) {
        tableNames.getString("TABLE_NAME") match {
          case "people" =>
            tablePersonFound = true
            tableNames.getString("TYPE_CAT") must beEqualTo("mongodb")
            tableNames.getString("REMARKS") must beEqualTo("COLLECTION")
            tableNames.getString("TABLE_TYPE") must beEqualTo("TABLE")
            tableNames.getString("TABLE_SCHEM") must beEqualTo("mongocamp-unit-test")
          case _ =>
        }
        tables += 1
      }
      tables must beGreaterThanOrEqualTo(1)
      val columnNames = connection.getMetaData.getColumns("%", "mongocamp-unit-test", "people", "")
      var columns           = 0
      while (columnNames.next()) {
        columnNames.getString("TABLE_CAT") must beEqualTo("mongodb")
        columnNames.getString("TABLE_NAME") must beEqualTo("people")
        columnNames.getString("TABLE_SCHEM") must beEqualTo("mongocamp-unit-test")
        val KeyDataType = "DATA_TYPE"
        columnNames.getString("COLUMN_NAME") match {
          case "_id" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.VARCHAR)
          case "id" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.BIGINT)
            columnNames.getInt("DECIMAL_DIGITS") must beEqualTo(0)
          case "guid" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.LONGVARCHAR)
          case "isActive" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.BOOLEAN)
          case "balance" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.DOUBLE)
            columnNames.getInt("DECIMAL_DIGITS") must beEqualTo(Int.MaxValue)
          case "registered" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.DATE)
          case "tags" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.ARRAY)
          case "friends" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.ARRAY)
          case "bestFriend" =>
            columnNames.getInt(KeyDataType) must beEqualTo(Types.JAVA_OBJECT)
          case _ =>
        }
        columns += 1
      }
      columns must beEqualTo(20)
      tablePersonFound must beTrue
    }

  }
}
