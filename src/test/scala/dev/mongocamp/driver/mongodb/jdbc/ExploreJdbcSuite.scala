package dev.mongocamp.driver.mongodb.jdbc

import java.sql.Types

class ExploreJdbcSuite extends BaseJdbcSuite {

  val schemaPattern: String = "mongocamp-unit-test$"

  test("Jdbc Connection should get table names") {
    val tableNames       = connection.getMetaData.getTables("%", schemaPattern, "", Array.empty)
    var tables           = 0
    var tablePersonFound = false
    while (tableNames.next()) {
      tableNames.getString("TABLE_NAME") match {
        case "people" =>
          tablePersonFound = true
          assertEquals(tableNames.getString("TYPE_CAT"), "mongodb")
          assertEquals(tableNames.getString("REMARKS"), "COLLECTION")
          assertEquals(tableNames.getString("TABLE_TYPE"), "TABLE")
          assertEquals(tableNames.getString("TABLE_SCHEM"), "mongocamp-unit-test")
        case _ =>
      }
      tables += 1
    }
    assert(tables >= 1)
    val columnNames = connection.getMetaData.getColumns("%", schemaPattern, "people", "")
    var columns     = 0
    while (columnNames.next()) {
      assertEquals(columnNames.getString("TABLE_CAT"), "mongodb")
      assertEquals(columnNames.getString("TABLE_NAME"), "people")
      assertEquals(columnNames.getString("TABLE_SCHEM"), "mongocamp-unit-test")
      val KeyDataType = "DATA_TYPE"
      columnNames.getString("COLUMN_NAME") match {
        case "_id" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.VARCHAR)
        case "id" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.BIGINT)
          assertEquals(columnNames.getInt("DECIMAL_DIGITS"), 0)
        case "guid" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.LONGVARCHAR)
        case "isActive" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.BOOLEAN)
        case "balance" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.DOUBLE)
          assertEquals(columnNames.getInt("DECIMAL_DIGITS"), Int.MaxValue)
        case "registered" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.DATE)
        case "tags" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.ARRAY)
        case "friends" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.ARRAY)
        case "bestFriend" =>
          assertEquals(columnNames.getInt(KeyDataType), Types.JAVA_OBJECT)
        case _ =>
      }
      columns += 1
    }
    assertEquals(columns, 20)
    assert(tablePersonFound)
  }
}
