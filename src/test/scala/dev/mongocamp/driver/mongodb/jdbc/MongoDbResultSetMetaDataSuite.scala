package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb.jdbc.resultSet.MongoDbResultSetMetaData
import dev.mongocamp.driver.mongodb.jdbc.statement.MongoPreparedStatement
import dev.mongocamp.driver.mongodb.MongoDAO
import java.sql.ResultSetMetaData
import munit.FunSuite
import org.mongodb.scala.Document

class MongoDbResultSetMetaDataSuite extends BaseJdbcSuite {

  var metaData: ResultSetMetaData = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    val preparedStatement2 = MongoPreparedStatement(connection.asInstanceOf[MongoJdbcConnection])
    preparedStatement2.executeUpdate("drop table testCollection")
    preparedStatement2.executeUpdate("insert into testCollection (intField, stringField, booleanField) values (1, 'test', true)")
    preparedStatement2.executeUpdate("insert into testCollection (intField, stringField, booleanField) values (2, 'test2', false)")
    metaData = preparedStatement2.executeQuery("select intField, booleanField, stringField from testCollection").getMetaData
  }

  test("getColumnCount should return the correct column count") {
    assertEquals(metaData.getColumnCount, 3)
  }

  test("getColumnLabel should return the correct column label") {
    assertEquals(metaData.getColumnLabel(1), "intField")
    assertEquals(metaData.getColumnLabel(2), "booleanField")
    assertEquals(metaData.getColumnLabel(3), "stringField")
  }

  test("getColumnName should return the correct column name") {
    assertEquals(metaData.getColumnName(1), "intField")
  }

  test("isAutoIncrement should return false") {
    assert(!metaData.isAutoIncrement(1))
  }

  test("isCaseSensitive should return true") {
    assert(metaData.isCaseSensitive(1))
  }

  test("isSearchable should return true") {
    assert(metaData.isSearchable(1))
  }

  test("isCurrency should return false") {
    assert(!metaData.isCurrency(1))
  }

  test("isNullable should return columnNullable") {
    assertEquals(metaData.isNullable(1), java.sql.ResultSetMetaData.columnNullable)
  }

  test("isSigned should return false") {
    assert(!metaData.isSigned(1))
  }

  test("getColumnDisplaySize should return Int.MaxValue") {
    assertEquals(metaData.getColumnDisplaySize(1), Int.MaxValue)
  }

  test("getSchemaName should return the database name") {
    assertEquals(metaData.getSchemaName(1), "mongocamp-unit-test")
  }

  test("getPrecision should return 0") {
    assertEquals(metaData.getPrecision(1), 0)
  }

  test("getScale should return 0") {
    assertEquals(metaData.getScale(1), 0)
  }

  test("getTableName should return the collection name") {
    assertEquals(metaData.getTableName(1), "testCollection")
  }

  test("getCatalogName should return the collection name") {
    assertEquals(metaData.getCatalogName(1), "testCollection")
  }

  test("getColumnType should return the correct SQL type") {
    assertEquals(metaData.getColumnType(1), java.sql.Types.BIGINT)
    assertEquals(metaData.getColumnType(2), java.sql.Types.BOOLEAN)
    assertEquals(metaData.getColumnType(3), java.sql.Types.VARCHAR)
  }

  test("getColumnTypeName should return the correct SQL type name") {
    assertEquals(metaData.getColumnTypeName(1), "BIGINT")
    assertEquals(metaData.getColumnTypeName(2), "BOOLEAN")
    assertEquals(metaData.getColumnTypeName(3), "VARCHAR")
  }

  test("isReadOnly should return false") {
    assert(!metaData.isReadOnly(1))
  }

  test("isWritable should return true") {
    assert(metaData.isWritable(1))
  }

  test("isDefinitelyWritable should return true") {
    assert(metaData.isDefinitelyWritable(1))
  }

  test("getColumnClassName should return the correct class name") {
    assertEquals(metaData.getColumnClassName(1), classOf[java.lang.Long].getName)
    assertEquals(metaData.getColumnClassName(2), classOf[java.lang.Boolean].getName)
    assertEquals(metaData.getColumnClassName(3), classOf[java.lang.String].getName)
  }

  test("unwrap should return null") {
    assertEquals(metaData.unwrap(classOf[Object]), null)
  }

  test("isWrapperFor should return false") {
    assert(!metaData.isWrapperFor(classOf[Object]))
  }

  test("getColumnIndex should return the correct index") {
    val metaData2 = metaData.asInstanceOf[MongoDbResultSetMetaData]
    assertEquals(metaData2.getColumnIndex("intField"), 1)
    assertEquals(metaData2.getColumnIndex("booleanField"), 2)
    assertEquals(metaData2.getColumnIndex("stringField"), 3)
  }
}
