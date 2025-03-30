package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.jdbc.resultSet.MongoDbResultSet
import java.sql._
import org.joda.time.DateTime
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Updates

class MongoDbResultSetSuite extends BaseJdbcSuite {

  def initializeResultSet(): ResultSet = {
    val data = List(
      Document("id" -> 1, "name" -> "test_name", "active"    -> true, "date" -> new DateTime("2021-01-01T00:00:00Z").toDate),
      Document("id" -> 2, "name" -> "another_name", "active" -> false)
    )
    val resultSet = new MongoDbResultSet(null, data, 0)
    resultSet
  }

  test("next() should move to the next row") {
    val resultSet = initializeResultSet()
    assert(resultSet.next())
    assertEquals(resultSet.getInt("id"), 1)
    assert(resultSet.next())
    assertEquals(resultSet.getInt("id"), 2)
    assert(!resultSet.next())
  }

  test("wasNull() should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.wasNull())
  }

  test("getString() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getString("name"), "test_name")
  }

  test("getBoolean() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assert(resultSet.getBoolean("active"))
  }

  test("getInt() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getInt("id"), 1)
  }

  test("getByte() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getByte("id"), 1.toByte)
  }

  test("getBytes() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getByte("id"), 1.toByte)
  }

  test("getShort() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getShort("id"), 1.toShort)
  }

  test("getFloat() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getFloat("id"), 1.toFloat)
  }

  test("getDouble() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getDouble("id"), 1.0)
  }

  test("getBigDecimal() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getBigDecimal("id"), new java.math.BigDecimal(1))
    assertEquals(resultSet.getBigDecimal("id", 1), new java.math.BigDecimal(1).setScale(1))
  }

  test("getDate() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getDate("date").toString, "2021-01-01")
  }

  test("getTime() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getTime("date"), new Time(new DateTime("2021-01-01T00:00:00Z").toDate.getTime))
  }

  test("getTimestamp() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getTimestamp("date").toInstant.toString, "2021-01-01T00:00:00Z")
  }

  test("isBeforeFirst() should return true initially") {
    val resultSet = initializeResultSet()
    assert(resultSet.isBeforeFirst)
  }

  test("isAfterLast() should return false initially") {
    val resultSet = initializeResultSet()
    assert(!resultSet.isAfterLast)
  }

  test("isFirst() should return true after first next()") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assert(resultSet.isFirst)
  }

  test("isLast() should return true after last next()") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.next()
    assert(resultSet.isLast)
  }

  test("getRow() should return the correct row number") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getRow, 1)
  }

  test("absolute() should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.absolute(1))
  }

  test("relative() should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.relative(1))
  }

  test("previous() should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.previous())
  }

  test("getFetchDirection() should return FETCH_FORWARD") {
    val resultSet = initializeResultSet()
    assertEquals(resultSet.getFetchDirection, ResultSet.FETCH_FORWARD)
  }

  test("getFetchSize() should return 1") {
    val resultSet = initializeResultSet()
    assertEquals(resultSet.getFetchSize, 1)
  }

  test("getType() should return TYPE_FORWARD_ONLY") {
    val resultSet = initializeResultSet()
    assertEquals(resultSet.getType, ResultSet.TYPE_FORWARD_ONLY)
  }

  test("getObject() should return the correct value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    assertEquals(resultSet.getObject("id"), 1.asInstanceOf[AnyRef])
  }

  test("updateString() should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateString("name", "updated_name")
    assertEquals(resultSet.getString("name"), "updated_name")
  }

  test("insertRow() should insert a new row") {
    intercept[SQLFeatureNotSupportedException] {
      val resultSet = initializeResultSet()
      resultSet.moveToInsertRow()
      resultSet.updateInt("id", 3)
      resultSet.updateString("name", "new_name")
      resultSet.updateBoolean("active", true)
      resultSet.insertRow()
    }
  }

  test("updateRow() should update the current row") {
    val resultSet = connection.createStatement().executeQuery("select _id, id, name, age from people where age < 30 order by id asc")
    resultSet.next()
    resultSet.updateString("name", "updated_name")
    resultSet.updateRow()
    assertEquals(resultSet.getString("name"), "updated_name")
    resultSet.refreshRow()
    val document = connection.asInstanceOf[MongoJdbcConnection].getDatabaseProvider.dao("people").find("id", resultSet.getLong("id")).result()
    assertEquals(document, resultSet.asInstanceOf[MongoDbResultSet].getDocument)
  }

  test("deleteRow() should delete the current row") {
    val resultSet = connection.createStatement().executeQuery("select _id, id, name, age from people where id = 10")
    resultSet.next()
    resultSet.deleteRow()
    assert(!resultSet.next())
    val document = connection.asInstanceOf[MongoJdbcConnection].getDatabaseProvider.dao("people").find("id", 10).resultOption()
    assert(document.isEmpty)
  }

  test("refreshRow() should refresh the current row") {
    val resultSet = connection.createStatement().executeQuery("select _id, id, name, age from people where id = 42")
    resultSet.next()
    assertEquals(resultSet.getString("name"), "Aisha Buckner")
    connection.asInstanceOf[MongoJdbcConnection].getDatabaseProvider.dao("people").updateOne(Map("id" -> 42), Updates.set("name", "updated_name")).result()
    resultSet.refreshRow()
    assertEquals(resultSet.getString("name"), "updated_name")
  }

  test("getMetaData() should return the correct metadata") {
    val resultSet = initializeResultSet()
    assert(resultSet.getMetaData != null)
  }

  test("findColumn() should return the correct column index") {
    val resultSet = initializeResultSet()
    assertEquals(resultSet.findColumn("id"), 1)
  }

  test("getWarnings() should return null") {
    val resultSet = initializeResultSet()
    assert(resultSet.getWarnings == null)
  }

  test("clearWarnings() should not throw an exception") {
    val resultSet = initializeResultSet()
    resultSet.clearWarnings()
  }

  test("getCursorName() should return null") {
    val resultSet = initializeResultSet()
    assert(resultSet.getCursorName == null)
  }

  test("getStatement() should return null") {
    val resultSet = initializeResultSet()
    assert(resultSet.getStatement == null)
  }

  test("unwrap() should return null") {
    val resultSet = initializeResultSet()
    assert(resultSet.unwrap(classOf[MongoDbResultSet]) == null)
  }

  test("isWrapperFor() should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.isWrapperFor(classOf[MongoDbResultSet]))
  }

  test("updateNull should update the value to null") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateNull(1)
    assert(resultSet.getObject(1) == null)
  }

  test("updateBoolean should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateBoolean(3, false)
    assert(!resultSet.getBoolean(3))
  }

  test("updateInt should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateInt(1, 42)
    assertEquals(resultSet.getInt(1), 42)
  }

  test("updateFloat should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateFloat(1, 42.toFloat)
    assertEquals(resultSet.getFloat(1), 42.toFloat)
  }

  test("updateBigDecimal should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateBigDecimal(1, new java.math.BigDecimal(42))
    assertEquals(resultSet.getBigDecimal(1), new java.math.BigDecimal(42))
  }

  test("updateString should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateString(2, "updated_name")
    assertEquals(resultSet.getString(2), "updated_name")
  }

  test("updateDate should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    val newDate = new Date(1622505600000L)
    resultSet.updateDate(4, newDate)
    assertEquals(resultSet.getDate(4), newDate)
  }

  test("updateTime should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    val newTime = new Time(1622505600000L)
    resultSet.updateTime(4, newTime)
    assertEquals(resultSet.getTime(4), newTime)
  }

  test("updateTimestamp should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    val newTimestamp = new Timestamp(1622505600000L)
    resultSet.updateTimestamp(4, newTimestamp)
    assertEquals(resultSet.getTimestamp(4), newTimestamp)
  }

  test("updateObject should update the value") {
    val resultSet = initializeResultSet()
    resultSet.next()
    resultSet.updateObject(1, 99)
    assertEquals(resultSet.getObject(1).asInstanceOf[Int], 99)
  }

  test("rowUpdated should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.rowUpdated())
  }

  test("rowInserted should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.rowInserted())
  }

  test("rowDeleted should return false") {
    val resultSet = initializeResultSet()
    assert(!resultSet.rowDeleted())
  }

  test("getConcurrency should throw SQLFeatureNotSupportedException") {
    val resultSet = initializeResultSet()
    intercept[SQLFeatureNotSupportedException](resultSet.getConcurrency)
    intercept[SQLFeatureNotSupportedException](resultSet.updateAsciiStream(99, null, 1))
    intercept[SQLFeatureNotSupportedException](resultSet.updateAsciiStream("updateAsciiStream", null, 1))
    intercept[SQLFeatureNotSupportedException](resultSet.updateBinaryStream(99, null, 1))
    intercept[SQLFeatureNotSupportedException](resultSet.updateBinaryStream("updateBinaryStream", null, 1))
    intercept[SQLFeatureNotSupportedException](resultSet.updateCharacterStream(99, null, 1))
    intercept[SQLFeatureNotSupportedException](resultSet.updateCharacterStream("updateCharacterStream", null, 1))

  }

  test("null values for not implemented get methods") {
    val resultSet = initializeResultSet()
    assertEquals(resultSet.getAsciiStream(1), null)
    assertEquals(resultSet.getUnicodeStream(1), null)
    assertEquals(resultSet.getBinaryStream(1), null)
    assertEquals(resultSet.getAsciiStream("id"), null)
    assertEquals(resultSet.getUnicodeStream("id"), null)
    assertEquals(resultSet.getBinaryStream("id"), null)
  }

}
