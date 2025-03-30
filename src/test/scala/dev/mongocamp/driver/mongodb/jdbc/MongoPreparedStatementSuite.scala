package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb.jdbc.statement.MongoPreparedStatement
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.Calendar

class MongoPreparedStatementSuite extends BaseJdbcSuite {
  var preparedStatement: MongoPreparedStatement = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    preparedStatement = MongoPreparedStatement(connection.asInstanceOf[MongoJdbcConnection])
    val preparedStatement2 = MongoPreparedStatement(connection.asInstanceOf[MongoJdbcConnection])
    preparedStatement2.executeUpdate("DELETE FROM table_name WHERE column2 = 123;")
  }

  test("execute should return false for null SQL") {
    assert(!preparedStatement.execute(null))
  }

  test("executeQuery should return empty result set for unsupported SQL") {
    val resultSet = preparedStatement.executeQuery("unsupported SQL")
    assert(resultSet != null)
    assertEquals(resultSet.next(), false)
  }

  test("setSql should set the SQL string") {
    preparedStatement.setSql("SELECT * FROM test")
    assertNotEquals(preparedStatement.executeQuery(), null)
  }

  test("setNull should set parameter to null") {
    preparedStatement.setNull(1, java.sql.Types.VARCHAR)
    assertEquals(preparedStatement.getString(1), "null")
  }

  test("setBoolean should set boolean parameter") {
    preparedStatement.setBoolean(1, true)
    assert(preparedStatement.getBoolean(1))
  }

  test("setByte should set byte parameter") {
    preparedStatement.setByte(1, 1.toByte)
    assertEquals(preparedStatement.getByte(1), 1.toByte)
  }

  test("setShort should set short parameter") {
    preparedStatement.setShort(1, 1.toShort)
    assertEquals(preparedStatement.getShort(1), 1.toShort)
  }

  test("setInt should set int parameter") {
    preparedStatement.setInt(1, 1)
    assertEquals(preparedStatement.getInt(1), 1)
  }

  test("setLong should set long parameter") {
    preparedStatement.setLong(1, 1L)
    assertEquals(preparedStatement.getLong(1), 1L)
  }

  test("setFloat should set float parameter") {
    preparedStatement.setFloat(1, 1.0f)
    assertEquals(preparedStatement.getFloat(1), 1.0f)
  }

  test("setDouble should set double parameter") {
    preparedStatement.setDouble(1, 1.0)
    assertEquals(preparedStatement.getDouble(1), 1.0)
  }

  test("setBigDecimal should set BigDecimal parameter") {
    preparedStatement.setBigDecimal(1, new java.math.BigDecimal("1.0"))
    assertEquals(preparedStatement.getBigDecimal(1), new java.math.BigDecimal(1.0))
  }

  test("setString should set string parameter") {
    preparedStatement.setString(1, "test")
    assertEquals(preparedStatement.getString(1), "test")
  }

  test("setBytes should set byte array parameter") {
    val bytes = Array[Byte](1.toByte, 2.toByte, 3.toByte)
    preparedStatement.setBytes(1, bytes)
    assertEquals(preparedStatement.getBytes(1).toList, bytes.toList)
  }

  test("setDate should set date parameter") {
    val date = new Date(System.currentTimeMillis())
    preparedStatement.setDate(1, date)
    assertEquals(preparedStatement.getDate(1), date)
  }

  test("setTime should set time parameter") {
    val time = new Time(System.currentTimeMillis())
    preparedStatement.setTime(1, time)
    assertEquals(preparedStatement.getTime(1), time)
  }

  test("setTimestamp should set timestamp parameter") {
    val timestamp = new Timestamp(System.currentTimeMillis())
    preparedStatement.setTimestamp(1, timestamp)
    assertEquals(preparedStatement.getTimestamp(1), timestamp)
  }

  test("clearParameters should clear all parameters") {
    preparedStatement.setString(1, "test")
    preparedStatement.clearParameters()
    assertEquals(preparedStatement.getString(1), null)
  }

  test("getConnection should return the connection") {
    assertEquals(preparedStatement.getConnection, connection)
  }

  test("getQueryTimeout should return the query timeout") {
    assertEquals(preparedStatement.getQueryTimeout, 10)
  }

  test("setQueryTimeout should set the query timeout") {
    preparedStatement.setQueryTimeout(20)
    assertEquals(preparedStatement.getQueryTimeout, 20)
  }

  test("getWarnings should return null") {
    assertEquals(preparedStatement.getWarnings, null)
  }

  test("clearWarnings should not throw exception") {
    preparedStatement.clearWarnings()
  }

  test("getResultSet should return the last result set") {
    assertNotEquals(preparedStatement.getResultSet, null)
  }

  test("getUpdateCount should return the last update count") {
    assertEquals(preparedStatement.getUpdateCount, -1)
    preparedStatement.executeUpdate(
      "INSERT INTO table_name (column1, column2, column3) VALUES ('value1', 123, '2022-01-01T00:00:00.000Z'), ('value2', 456, '2022-02-01T00:00:00.000Z');"
    )
    assertEquals(preparedStatement.getUpdateCount, 2)
    preparedStatement.executeUpdate("Update table_name SET column1 = 'value3' WHERE column2 = 123;")
    assertEquals(preparedStatement.getUpdateCount, 1)
    preparedStatement.executeUpdate("DELETE FROM table_name WHERE column2 = 123;")
    assertEquals(preparedStatement.getUpdateCount, 1)
  }

  test("getMoreResults should return false") {
    assert(!preparedStatement.getMoreResults)
  }

  test("getFetchDirection should return FETCH_FORWARD") {
    assertEquals(preparedStatement.getFetchDirection, java.sql.ResultSet.FETCH_FORWARD)
  }

  test("getFetchSize should return -1") {
    assertEquals(preparedStatement.getFetchSize, -1)
  }

  test("getResultSetType should return TYPE_FORWARD_ONLY") {
    assertEquals(preparedStatement.getResultSetType, java.sql.ResultSet.TYPE_FORWARD_ONLY)
  }

  test("getGeneratedKeys should return null") {
    assertEquals(preparedStatement.getGeneratedKeys, null)
  }

  test("getResultSetHoldability should return 0") {
    assertEquals(preparedStatement.getResultSetHoldability, 0)
  }

  test("isPoolable should return false") {
    assert(!preparedStatement.isPoolable)
  }

  test("isCloseOnCompletion should return false") {
    assert(!preparedStatement.isCloseOnCompletion)
  }

  test("wasNull should return false") {
    assert(!preparedStatement.wasNull())
  }

  test("getObject should return the parameter value") {
    preparedStatement.setString(1, "test")
    assertEquals(preparedStatement.getObject(1), "test")
  }

  test("getURL should return the URL parameter") {
    preparedStatement.setString(1, "http://example.com")
    assertEquals(preparedStatement.getURL(1), new java.net.URL("http://example.com"))
  }

  test("setObject should return an string") {
    preparedStatement.setObject(1, "value")
    assertEquals(preparedStatement.getString(1), "value")
    preparedStatement.setObject(1, "value1", java.sql.Types.VARCHAR)
    assertEquals(preparedStatement.getString(1), "value1")
    preparedStatement.setObject(1, "value2", java.sql.Types.VARCHAR, 0)
    assertEquals(preparedStatement.getString(1), "value2")
    preparedStatement.setObject(1, null)
    assertEquals(preparedStatement.getString(1), "null")
    preparedStatement.setObject(1, List(1, 2, 3))
    assertEquals(preparedStatement.getString(1), "[1,2,3]")
    preparedStatement.setObject(1, List("hallo", "world"))
    assertEquals(preparedStatement.getString(1), "[\"hallo\",\"world\"]")
  }

  test("set URL should set the URL parameter") {
    preparedStatement.setURL(1, new java.net.URL("http://example.com"))
    assertEquals(preparedStatement.getURL(1), new java.net.URL("http://example.com"))
    assertEquals(preparedStatement.getString(1), "http://example.com")
  }

  import java.sql.SQLFeatureNotSupportedException

  test("All unsupported methods should throw SQLFeatureNotSupportedException") {
    val preparedStatement = new MongoPreparedStatement(connection.asInstanceOf[MongoJdbcConnection])

    def assertThrowsFeatureNotSupportedException(f: => Unit): Unit = {
      intercept[SQLFeatureNotSupportedException](f)
    }

    assertThrowsFeatureNotSupportedException(preparedStatement.getString("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getBoolean("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getByte("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getShort("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getInt("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getLong("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getFloat("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getDouble("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getBytes("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getDate("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getTime("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getTimestamp("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getObject("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getBigDecimal("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getObject("param", classOf[Any]))
    assertThrowsFeatureNotSupportedException(preparedStatement.getRef("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getBlob("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getClob("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getArray("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getDate("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.getTime("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.getTimestamp("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.getURL("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getRowId(1))
    assertThrowsFeatureNotSupportedException(preparedStatement.getRowId("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.setRowId("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNString("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNCharacterStream("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNClob("param", null.asInstanceOf[java.sql.NClob]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setClob("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBlob("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNClob("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.getNClob(1))
    assertThrowsFeatureNotSupportedException(preparedStatement.getNClob("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.setSQLXML("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.getSQLXML(1))
    assertThrowsFeatureNotSupportedException(preparedStatement.getSQLXML("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getNString(1))
    assertThrowsFeatureNotSupportedException(preparedStatement.getNString("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getNCharacterStream(1))
    assertThrowsFeatureNotSupportedException(preparedStatement.getNCharacterStream("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.getCharacterStream(1))
    assertThrowsFeatureNotSupportedException(preparedStatement.getCharacterStream("param"))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBlob("param", null.asInstanceOf[java.sql.Blob]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setClob("param", null.asInstanceOf[java.sql.NClob]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setAsciiStream("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBinaryStream("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setCharacterStream("param", null, 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setAsciiStream("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBinaryStream("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setCharacterStream("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNCharacterStream("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setClob("param", null.asInstanceOf[java.sql.NClob]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBlob("param", null.asInstanceOf[java.sql.Blob]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNClob("param", null.asInstanceOf[java.sql.NClob]))
    assertThrowsFeatureNotSupportedException(preparedStatement.getObject(1, classOf[Any]))
    assertThrowsFeatureNotSupportedException(preparedStatement.getObject("param", classOf[Any]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setURL("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNull("param", 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBoolean("param", false))
    assertThrowsFeatureNotSupportedException(preparedStatement.setByte("param", 0.toByte))
    assertThrowsFeatureNotSupportedException(preparedStatement.setShort("param", 0.toShort))
    assertThrowsFeatureNotSupportedException(preparedStatement.setInt("param", 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setLong("param", 0L))
    assertThrowsFeatureNotSupportedException(preparedStatement.setFloat("param", 0.0f))
    assertThrowsFeatureNotSupportedException(preparedStatement.setDouble("param", 0.0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBigDecimal("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setString("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBytes("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setDate("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setTime("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setTimestamp("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setAsciiStream("param", null, 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBinaryStream("param", null, 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setObject("param", null, 0, 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setObject("param", null, 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setObject("param", null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setCharacterStream("param", null, 0))
    assertThrowsFeatureNotSupportedException(preparedStatement.setDate("param", null, null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setTime("param", null, null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setTimestamp("param", null, null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNull("param", 0, null))
    assertThrowsFeatureNotSupportedException(preparedStatement.setClob("param", null.asInstanceOf[Reader]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setNClob("param", null.asInstanceOf[Reader]))
    assertThrowsFeatureNotSupportedException(preparedStatement.setBlob("param", null.asInstanceOf[InputStream]))
  }

  test("set values should not throw exception") {
    preparedStatement.addBatch()
    preparedStatement.setCharacterStream(1, null, 0)
    preparedStatement.setRef(1, null)
    preparedStatement.setBlob(1, null.asInstanceOf[java.sql.Blob])
    preparedStatement.setClob(1, null.asInstanceOf[java.sql.Clob])
    preparedStatement.setDate(1, new Date(0), Calendar.getInstance())
    preparedStatement.setTime(1, new Time(0), Calendar.getInstance())
    preparedStatement.setTimestamp(1, new Timestamp(0), Calendar.getInstance())
    preparedStatement.setNull(1, 0, "typeName")
    preparedStatement.setURL(1, new URL("http://example.com"))
    preparedStatement.setRowId(1, null)
    preparedStatement.setNString(1, null)
    preparedStatement.setNCharacterStream(1, null, 0L)
    preparedStatement.setNClob(1, null.asInstanceOf[java.sql.NClob])
    preparedStatement.setClob(1, null, 0L)
    preparedStatement.setBlob(1, null, 0L)
    preparedStatement.setNClob(1, null, 0L)
    preparedStatement.setSQLXML(1, null)
    preparedStatement.setAsciiStream(1, null, 0L)
    preparedStatement.setBinaryStream(1, null, 0L)
    preparedStatement.setCharacterStream(1, null, 0L)
    preparedStatement.setAsciiStream(1, null)
    preparedStatement.setBinaryStream(1, null)
    preparedStatement.setCharacterStream(1, null)
    preparedStatement.setNCharacterStream(1, null)
    preparedStatement.setClob(1, null.asInstanceOf[java.sql.Clob])
    preparedStatement.setBlob(1, null.asInstanceOf[java.sql.Blob])
    preparedStatement.setNClob(1, null.asInstanceOf[java.sql.NClob])
    preparedStatement.setArray(1, null.asInstanceOf[java.sql.Array])
    preparedStatement.setAsciiStream(1, null.asInstanceOf[InputStream], 1)
    preparedStatement.setUnicodeStream(1, null.asInstanceOf[InputStream], 1)
    preparedStatement.setBinaryStream(1, null.asInstanceOf[InputStream], 1)
    assertEquals(preparedStatement.getMetaData, null)
  }
}
