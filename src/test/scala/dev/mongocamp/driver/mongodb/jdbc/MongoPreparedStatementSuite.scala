package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb.jdbc.statement.MongoPreparedStatement
import java.io.InputStream
import java.io.Reader
import java.net.URL
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

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
    val date = Date.valueOf("2021-01-01")
    preparedStatement.setDate(1, date)
    assertEquals(preparedStatement.getDate(1), date)
  }

  private val storedInstant = java.time.Instant.parse("2021-01-01T00:00:00Z")

  private def withDefaultTimeZone(zoneId: String)(f: => Unit): Unit = {
    val defaultTimeZone = TimeZone.getDefault
    try {
      TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
      f
    }
    finally TimeZone.setDefault(defaultTimeZone)
  }

  private def calendar(zoneId: String): Calendar = Calendar.getInstance(TimeZone.getTimeZone(zoneId))

  test("getDate should return the date normalized to midnight in the default time zone") {
    withDefaultTimeZone("America/New_York") {
      preparedStatement.setTimestamp(1, Timestamp.from(storedInstant))
      assertEquals(preparedStatement.getDate(1).toString, "2020-12-31")
      assertEquals(preparedStatement.getDate(1), Date.valueOf("2020-12-31"))
    }
    withDefaultTimeZone("Asia/Tokyo") {
      preparedStatement.setTimestamp(1, Timestamp.from(storedInstant))
      assertEquals(preparedStatement.getDate(1), Date.valueOf("2021-01-01"))
    }
  }

  test("getDate with calendar should use the time zone of the calendar") {
    withDefaultTimeZone("America/New_York") {
      preparedStatement.setTimestamp(1, Timestamp.from(storedInstant))
      assertEquals(preparedStatement.getDate(1, calendar("UTC")), Date.valueOf("2021-01-01"))
      assertEquals(preparedStatement.getDate(1, calendar("America/Los_Angeles")), Date.valueOf("2020-12-31"))
      assertEquals(preparedStatement.getDate(1, null), Date.valueOf("2020-12-31"))
    }
  }

  test("setDate with calendar should store midnight in the time zone of the calendar") {
    preparedStatement.setDate(1, Date.valueOf("2021-01-01"), calendar("UTC"))
    assertEquals(preparedStatement.getString(1), "2021-01-01T00:00:00Z")
    assertEquals(preparedStatement.getDate(1, calendar("UTC")), Date.valueOf("2021-01-01"))
    preparedStatement.setDate(1, Date.valueOf("2021-01-01"), calendar("Asia/Tokyo"))
    assertEquals(preparedStatement.getString(1), "2020-12-31T15:00:00Z")
    assertEquals(preparedStatement.getDate(1, calendar("Asia/Tokyo")), Date.valueOf("2021-01-01"))
    preparedStatement.setDate(1, Date.valueOf("2021-01-01"), null)
    assertEquals(preparedStatement.getDate(1), Date.valueOf("2021-01-01"))
    preparedStatement.setDate(1, null, calendar("UTC"))
    assertEquals(preparedStatement.getDate(1), null)
  }

  test("timestamp with calendar should keep the instant") {
    preparedStatement.setTimestamp(1, Timestamp.from(storedInstant), calendar("Asia/Tokyo"))
    assertEquals(preparedStatement.getString(1), "2021-01-01T00:00:00Z")
    assertEquals(preparedStatement.getTimestamp(1, calendar("America/New_York")).toInstant, storedInstant)
  }

  test("getTime should return the time on 1970-01-01 in the default time zone") {
    withDefaultTimeZone("America/New_York") {
      preparedStatement.setTimestamp(1, Timestamp.from(storedInstant))
      assertEquals(preparedStatement.getTime(1), Time.valueOf("19:00:00"))
      assertEquals(preparedStatement.getTime(1).toString, "19:00:00")
    }
  }

  test("getTime with calendar should use the time zone of the calendar") {
    withDefaultTimeZone("America/New_York") {
      preparedStatement.setTimestamp(1, Timestamp.from(storedInstant))
      assertEquals(preparedStatement.getTime(1, calendar("UTC")), Time.valueOf("00:00:00"))
      assertEquals(preparedStatement.getTime(1, calendar("Asia/Tokyo")), Time.valueOf("09:00:00"))
      assertEquals(preparedStatement.getTime(1, null), Time.valueOf("19:00:00"))
    }
  }

  test("setTime with calendar should store the time on 1970-01-01 in the time zone of the calendar") {
    val time = Time.valueOf("10:15:30")
    preparedStatement.setTime(1, time, calendar("UTC"))
    assertEquals(preparedStatement.getString(1), "1970-01-01T10:15:30Z")
    assertEquals(preparedStatement.getTime(1, calendar("UTC")), time)
    preparedStatement.setTime(1, time, calendar("Asia/Tokyo"))
    assertEquals(preparedStatement.getString(1), "1970-01-01T01:15:30Z")
    assertEquals(preparedStatement.getTime(1, calendar("Asia/Tokyo")), time)
    preparedStatement.setTime(1, time, null)
    assertEquals(preparedStatement.getTime(1), time)
    preparedStatement.setTime(1, null, calendar("UTC"))
    assertEquals(preparedStatement.getTime(1), null)
  }

  test("date getters should return null for missing or invalid parameters") {
    preparedStatement.clearParameters()
    assertEquals(preparedStatement.getDate(1), null)
    assertEquals(preparedStatement.getDate(1, calendar("UTC")), null)
    assertEquals(preparedStatement.getTime(1), null)
    assertEquals(preparedStatement.getTimestamp(1), null)
    preparedStatement.setString(1, "no date")
    assertEquals(preparedStatement.getDate(1), null)
    assertEquals(preparedStatement.getTimestamp(1), null)
  }

  test("setObject should convert date values to ISO instant") {
    val millis = storedInstant.toEpochMilli
    List[Any](
      new java.util.Date(millis),
      new Date(millis),
      new Time(millis),
      new Timestamp(millis),
      storedInstant,
      storedInstant.atZone(java.time.ZoneId.of("Asia/Tokyo")),
      storedInstant.atOffset(java.time.ZoneOffset.ofHours(-5)),
      new org.joda.time.DateTime(millis),
      new org.joda.time.Instant(millis)
    ).foreach(
      value => {
        preparedStatement.setObject(1, value)
        assertEquals(preparedStatement.getString(1), "2021-01-01T00:00:00Z", value.getClass.getName)
        assertEquals(preparedStatement.getTimestamp(1).toInstant, storedInstant, value.getClass.getName)
      }
    )
  }

  test("setObject should convert local date values in the default time zone") {
    val localDate     = java.time.LocalDate.of(2021, 1, 1)
    val localDateTime = java.time.LocalDateTime.of(2021, 1, 1, 10, 15, 30)
    val zoneId        = java.time.ZoneId.systemDefault()
    preparedStatement.setObject(1, localDate)
    assertEquals(preparedStatement.getString(1), localDate.atStartOfDay(zoneId).toInstant.toString)
    assertEquals(preparedStatement.getDate(1), Date.valueOf(localDate))
    preparedStatement.setObject(1, localDateTime)
    assertEquals(preparedStatement.getString(1), localDateTime.atZone(zoneId).toInstant.toString)
    preparedStatement.setObject(1, new org.joda.time.LocalDate(2021, 1, 1))
    assertEquals(preparedStatement.getDate(1), Date.valueOf(localDate))
    preparedStatement.setObject(1, new org.joda.time.LocalDateTime(2021, 1, 1, 10, 15, 30))
    assertEquals(preparedStatement.getString(1), localDateTime.atZone(zoneId).toInstant.toString)
  }

  test("setObject with UUID should find the document") {
    val statement = connection.prepareStatement("select name from `mongocamp-unit-test`.people where guid = ?")
    statement.setObject(1, UUID.fromString("38ede9f0-f8f7-4d78-98b9-c983eaeeac3f"))
    val result = statement.executeQuery()
    assert(result.next())
    assertEquals(result.getString("name"), "Latasha Mcmillan")
    assert(!result.next())
  }

  test("setDate with calendar should find the documents of the day") {
    val statement = connection.prepareStatement("select name from `mongocamp-unit-test`.people where registered >= ? and registered < ?")
    statement.setDate(1, Date.valueOf("2014-04-19"), calendar("UTC"))
    statement.setDate(2, Date.valueOf("2014-04-20"), calendar("UTC"))
    val result = statement.executeQuery()
    var count  = 0
    while (result.next())
      count += 1
    assertEquals(count, 3)
  }

  test("setTimestamp should find the document") {
    val statement = connection.prepareStatement("select name from `mongocamp-unit-test`.people where registered = ?")
    statement.setTimestamp(1, Timestamp.from(java.time.Instant.parse("2014-04-19T22:44:27Z")))
    val result = statement.executeQuery()
    assert(result.next())
    assertEquals(result.getString("name"), "Latasha Mcmillan")
    assert(!result.next())
  }

  test("setTime should set time parameter") {
    val time = Time.valueOf("10:15:30")
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
