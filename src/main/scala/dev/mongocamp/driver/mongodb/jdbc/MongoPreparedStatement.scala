package dev.mongocamp.driver.mongodb.jdbc

import java.io.{InputStream, Reader}
import java.net.URL
import java.sql
import java.sql.{Blob, Clob, Connection, Date, NClob, ParameterMetaData, PreparedStatement, Ref, ResultSet, ResultSetMetaData, RowId, SQLException, SQLFeatureNotSupportedException, SQLWarning, SQLXML, Time, Timestamp}
import java.util.Calendar

class MongoPreparedStatement(connection: MongoJdbcConnection, private var query: String) extends PreparedStatement {
  private var lastResultSet: ResultSet = null
  private var _isClosed = false
  private var maxRows = -1
  private var fetchSize = -1

  override def executeQuery(sql: String): ResultSet = {
    checkClosed()
    query = sql
    if (lastResultSet != null && !lastResultSet.isClosed) {
      lastResultSet.close();
    }
    if (query == null) {
      throw new SQLException("Null statement.");
    }
    // todo: execute and generate result set
    // lastResultSet = connection.getScriptEngine().execute(query, fetchSize);
    lastResultSet
  }

  override def executeUpdate(sql: String): Int = ???

  override def executeQuery(): ResultSet = {
    execute(query)
    lastResultSet
  }

  override def execute(sql: String): Boolean = {
    executeQuery(sql)
    lastResultSet != null
  }

  override def executeUpdate(): Int = executeUpdate(query)

  override def setNull(parameterIndex: Int, sqlType: Int): Unit = {}

  override def setBoolean(parameterIndex: Int, x: Boolean): Unit = {}

  override def setByte(parameterIndex: Int, x: Byte): Unit = {}

  override def setShort(parameterIndex: Int, x: Short): Unit = {}

  override def setInt(parameterIndex: Int, x: Int): Unit = {}

  override def setLong(parameterIndex: Int, x: Long): Unit = {}

  override def setFloat(parameterIndex: Int, x: Float): Unit = {}

  override def setDouble(parameterIndex: Int, x: Double): Unit = {}

  override def setBigDecimal(parameterIndex: Int, x: java.math.BigDecimal): Unit = {}

  override def setString(parameterIndex: Int, x: String): Unit = {}

  override def setBytes(parameterIndex: Int, x: Array[Byte]): Unit = {}

  override def setDate(parameterIndex: Int, x: Date): Unit = {}

  override def setTime(parameterIndex: Int, x: Time): Unit = {}

  override def setTimestamp(parameterIndex: Int, x: Timestamp): Unit = {}

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {}

  override def setUnicodeStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {}

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {}

  override def clearParameters(): Unit = {}

  override def setObject(parameterIndex: Int, x: Any, targetSqlType: Int): Unit = {}

  override def setObject(parameterIndex: Int, x: Any): Unit = {}

  override def execute(): Boolean = {
    query != null && execute(query)
  }

  override def addBatch(): Unit = {}

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Int): Unit = {}

  override def setRef(parameterIndex: Int, x: Ref): Unit = {}

  override def setBlob(parameterIndex: Int, x: Blob): Unit = {}

  override def setClob(parameterIndex: Int, x: Clob): Unit = {}

  override def setArray(parameterIndex: Int, x: sql.Array): Unit = {}

  override def getMetaData: ResultSetMetaData = {
    null
  }

  override def setDate(parameterIndex: Int, x: Date, cal: Calendar): Unit = {}

  override def setTime(parameterIndex: Int, x: Time, cal: Calendar): Unit = {}

  override def setTimestamp(parameterIndex: Int, x: Timestamp, cal: Calendar): Unit = {}

  override def setNull(parameterIndex: Int, sqlType: Int, typeName: String): Unit = {}

  override def setURL(parameterIndex: Int, x: URL): Unit = {}

  override def getParameterMetaData: ParameterMetaData = null

  override def setRowId(parameterIndex: Int, x: RowId): Unit = {}

  override def setNString(parameterIndex: Int, value: String): Unit = {}

  override def setNCharacterStream(parameterIndex: Int, value: Reader, length: Long): Unit = {}

  override def setNClob(parameterIndex: Int, value: NClob): Unit = {}

  override def setClob(parameterIndex: Int, reader: Reader, length: Long): Unit = {}

  override def setBlob(parameterIndex: Int, inputStream: InputStream, length: Long): Unit = {}

  override def setNClob(parameterIndex: Int, reader: Reader, length: Long): Unit = {}

  override def setSQLXML(parameterIndex: Int, xmlObject: SQLXML): Unit = {}

  override def setObject(parameterIndex: Int, x: Any, targetSqlType: Int, scaleOrLength: Int): Unit = {}

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Long): Unit = {}

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Long): Unit = {}

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Long): Unit = {}

  override def setAsciiStream(parameterIndex: Int, x: InputStream): Unit = {}

  override def setBinaryStream(parameterIndex: Int, x: InputStream): Unit = {}

  override def setCharacterStream(parameterIndex: Int, reader: Reader): Unit = {}

  override def setNCharacterStream(parameterIndex: Int, value: Reader): Unit = {}

  override def setClob(parameterIndex: Int, reader: Reader): Unit = {}

  override def setBlob(parameterIndex: Int, inputStream: InputStream): Unit = {}

  override def setNClob(parameterIndex: Int, reader: Reader): Unit = {}

  override def close(): Unit = {
    _isClosed = true
    if (lastResultSet == null || lastResultSet.isClosed) {
      return
    }
    lastResultSet.close()
  }

  override def getMaxFieldSize: Int = {
    0
  }

  override def setMaxFieldSize(max: Int): Unit = {}

  override def getMaxRows: Int = maxRows

  override def setMaxRows(max: Int): Unit = maxRows = max

  override def setEscapeProcessing(enable: Boolean): Unit = {}

  override def getQueryTimeout: Int = {
    checkClosed()
    throw new SQLFeatureNotSupportedException("MongoDB provides no support for query timeouts.")
  }

  override def setQueryTimeout(seconds: Int): Unit = {
    checkClosed()
    throw new SQLFeatureNotSupportedException("MongoDB provides no support for query timeouts.")
  }

  override def cancel(): Unit = {
    checkClosed()
    throw new SQLFeatureNotSupportedException("MongoDB provides no support for query timeouts.")
  }

  override def getWarnings: SQLWarning = {
    checkClosed()
    null
  }

  override def clearWarnings(): Unit = {
    checkClosed()
  }

  override def setCursorName(name: String): Unit = {
    checkClosed()
  }

  override def getResultSet: ResultSet = {
    checkClosed()
    lastResultSet;
  }

  override def getUpdateCount: Int = {
    checkClosed()
    -1
  }

  override def getMoreResults: Boolean = false

  override def setFetchDirection(direction: Int): Unit = {}

  override def getFetchDirection: Int = ResultSet.FETCH_FORWARD

  override def setFetchSize(rows: Int): Unit = {
    if (rows <= 1) {
      throw new SQLException("Fetch size must be > 1. Actual: " + rows)
    }
    fetchSize = rows
  }

  override def getFetchSize: Int = fetchSize

  override def getResultSetConcurrency: Int = throw new SQLFeatureNotSupportedException();

  override def getResultSetType: Int = ResultSet.TYPE_FORWARD_ONLY

  override def addBatch(sql: String): Unit = {}


  override def clearBatch(): Unit = {}

  override def executeBatch(): Array[Int] = {
    checkClosed()
    null
  }

  override def getConnection: Connection = {
    checkClosed()
    connection
  }

  override def getMoreResults(current: Int): Boolean = {
    checkClosed()
    false
  }

  override def getGeneratedKeys: ResultSet = {
    checkClosed()
    null
  }

  override def executeUpdate(sql: String, autoGeneratedKeys: Int): Int = {
    checkClosed()
    0
  }

  override def executeUpdate(sql: String, columnIndexes: Array[Int]): Int = {
    checkClosed()
    0
  }

  override def executeUpdate(sql: String, columnNames: Array[String]): Int = {
    checkClosed()
    0
  }

  override def execute(sql: String, autoGeneratedKeys: Int): Boolean = {
    checkClosed()
    false
  }

  override def execute(sql: String, columnIndexes: Array[Int]): Boolean = {
    checkClosed()
    false
  }

  override def execute(sql: String, columnNames: Array[String]): Boolean = {
    checkClosed()
    false
  }

  override def getResultSetHoldability: Int = 0

  override def isClosed: Boolean = _isClosed

  override def setPoolable(poolable: Boolean): Unit = {}

  override def isPoolable: Boolean = false

  override def closeOnCompletion(): Unit = {}

  override def isCloseOnCompletion: Boolean = false

  override def unwrap[T](iface: Class[T]): T = null.asInstanceOf[T]

  override def isWrapperFor(iface: Class[_]): Boolean = false

  private def checkClosed(): Unit = {
    if (isClosed) {
      throw new SQLAlreadyClosedException(this.getClass.getSimpleName)
    }
  }
}
