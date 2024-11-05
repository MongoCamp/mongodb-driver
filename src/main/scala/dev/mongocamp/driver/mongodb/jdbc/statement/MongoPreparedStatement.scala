package dev.mongocamp.driver.mongodb.jdbc.statement

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.{ Converter, GenericObservable }
import dev.mongocamp.driver.mongodb.exception.SqlCommandNotSupportedException
import dev.mongocamp.driver.mongodb.jdbc.{ MongoJdbcCloseable, MongoJdbcConnection }
import dev.mongocamp.driver.mongodb.jdbc.resultSet.MongoDbResultSet
import dev.mongocamp.driver.mongodb.sql.MongoSqlQueryHolder
import org.joda.time.DateTime

import java.io.{ InputStream, Reader }
import java.net.URL
import java.{ sql, util }
import java.sql.{
  Blob,
  CallableStatement,
  Clob,
  Connection,
  Date,
  NClob,
  ParameterMetaData,
  PreparedStatement,
  Ref,
  ResultSet,
  ResultSetMetaData,
  RowId,
  SQLWarning,
  SQLXML,
  Time,
  Timestamp
}
import java.util.Calendar
import scala.collection.mutable

case class MongoPreparedStatement(connection: MongoJdbcConnection) extends CallableStatement with MongoJdbcCloseable with LazyLogging {

  def this(connection: MongoJdbcConnection, sql: String) = {
    this(connection)
    setSql(sql)
  }

  private var _queryTimeout: Int        = 10
  private var _sql: String              = null
  private var _lastResultSet: ResultSet = null
  private var _lastUpdateCount: Int     = -1
  private lazy val parameters           = mutable.Map[Int, String]()

  override def execute(sql: String): Boolean = {
    checkClosed()
    if (sql != null) {
      try {
        val response = MongoSqlQueryHolder(sql).run(connection.getDatabaseProvider).results(getQueryTimeout)
        true
      }
      catch {
        case e: Exception =>
          false
      }
    }
    else {
      false
    }
  }

  override def executeQuery(sql: String): ResultSet = {
    checkClosed()
    val queryHolder: MongoSqlQueryHolder =
      try MongoSqlQueryHolder(sql)
      catch {
        case e: SqlCommandNotSupportedException =>
          logger.error(e.getMessage, e)
          null
      }
    if (queryHolder == null) {
      new MongoDbResultSet(null, List.empty, 0)
    }
    else {
      var response = queryHolder.run(connection.getDatabaseProvider).results(getQueryTimeout)
      if (response.isEmpty && queryHolder.hasFunctionCallInSelect) {
        val emptyDocument = mutable.Map[String, Any]()
        queryHolder.getKeysForEmptyDocument.foreach(key => emptyDocument.put(key, null))
        val doc = Converter.toDocument(emptyDocument.toMap)
        response = Seq(doc)
      }
      val collectionName = Option(queryHolder.getCollection).map(c => connection.getDatabaseProvider.dao(c))
      if (!sql.toLowerCase().contains("_id")) {
        response = response.map(doc => {
          val newDoc = doc - "_id"
          newDoc
        })
      }
      val resultSet = new MongoDbResultSet(collectionName.orNull, response.toList, getQueryTimeout)
      _lastResultSet = resultSet
      resultSet
    }
  }

  def setSql(sql: String): Unit = {
    _sql = sql
  }

  override def executeQuery(): ResultSet = {
    checkClosed()
    executeQuery(replaceParameters(_sql))
  }

  override def executeUpdate(): Int = {
    executeUpdate(replaceParameters(_sql))
  }

  override def setNull(parameterIndex: Int, sqlType: Int): Unit = {
    checkClosed()
    setObject(parameterIndex, null)
  }

  override def setArray(parameterIndex: Int, x: java.sql.Array): Unit = {
    checkClosed()
  }

  override def setBoolean(parameterIndex: Int, x: Boolean): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setByte(parameterIndex: Int, x: Byte): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setShort(parameterIndex: Int, x: Short): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setInt(parameterIndex: Int, x: Int): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setLong(parameterIndex: Int, x: Long): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setFloat(parameterIndex: Int, x: Float): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setDouble(parameterIndex: Int, x: Double): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setBigDecimal(parameterIndex: Int, x: java.math.BigDecimal): Unit = {
    checkClosed()
    setObject(parameterIndex, x.doubleValue())
  }

  override def setString(parameterIndex: Int, x: String): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'$x'")
  }

  override def setBytes(parameterIndex: Int, x: Array[Byte]): Unit = {
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setDate(parameterIndex: Int, x: Date): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'${x.toInstant.toString}'")
  }

  override def setTime(parameterIndex: Int, x: Time): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'${x.toInstant.toString}'")
  }

  override def setTimestamp(parameterIndex: Int, x: Timestamp): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'${x.toInstant.toString}'")
  }

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {
    checkClosed()
  }

  override def setUnicodeStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {
    checkClosed()
  }

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Int): Unit = {
    checkClosed()
  }

  private def replaceParameters(sql: String): String = {
    var newSql     = ""
    var paramCount = 1
    sql.foreach(c => {
      var replace = false
      if (c == '?') {
        if (parameters.contains(paramCount)) {
          newSql += parameters(paramCount)
          replace = true
        }
        paramCount += 1
      }
      if (!replace) {
        newSql += c
      }
    })
    newSql
  }

  override def clearParameters(): Unit = {
    checkClosed()
    parameters.clear()
  }

  override def setObject(parameterIndex: Int, x: Any, targetSqlType: Int): Unit = {
    setObject(parameterIndex, x)
  }

  override def setObject(parameterIndex: Int, x: Any): Unit = {
    checkClosed()
    x match {
      case d: Date =>
        parameters.put(parameterIndex, s"'${d.toInstant.toString}'")
      case d: DateTime =>
        parameters.put(parameterIndex, s"'${d.toInstant.toString}'")
      case t: Time =>
        parameters.put(parameterIndex, s"'${t.toInstant.toString}'")
      case a: Array[Byte] =>
        parameters.put(parameterIndex, a.mkString("[", ",", "]"))
      case a: Iterable[_] =>
        parameters.put(parameterIndex, a.mkString("[", ",", "]"))
      case _ =>
        parameters.put(parameterIndex, x.toString)
    }
  }

  override def execute(): Boolean = {
    execute(replaceParameters(_sql))
  }

  override def addBatch(): Unit = {
    checkClosed()
  }

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Int): Unit = {
    checkClosed()
  }

  override def setRef(parameterIndex: Int, x: Ref): Unit = {
    checkClosed()
  }

  override def setBlob(parameterIndex: Int, x: Blob): Unit = {
    checkClosed()
  }

  override def setClob(parameterIndex: Int, x: Clob): Unit = {
    checkClosed()
  }

  override def getMetaData: ResultSetMetaData = {
    checkClosed()
    null
  }

  override def setDate(parameterIndex: Int, x: Date, cal: Calendar): Unit = {
    setDate(parameterIndex, x)
  }

  override def setTime(parameterIndex: Int, x: Time, cal: Calendar): Unit = {
    setTime(parameterIndex, x)
  }

  override def setTimestamp(parameterIndex: Int, x: Timestamp, cal: Calendar): Unit = {
    setTimestamp(parameterIndex, x)
  }

  override def setNull(parameterIndex: Int, sqlType: Int, typeName: String): Unit = {
    setNull(parameterIndex, sqlType)
  }

  override def setURL(parameterIndex: Int, x: URL): Unit = {
    sqlFeatureNotSupported()
  }

  override def getParameterMetaData: ParameterMetaData = {
    sqlFeatureNotSupported()
  }

  override def setRowId(parameterIndex: Int, x: RowId): Unit = {
    checkClosed()
  }

  override def setNString(parameterIndex: Int, value: String): Unit = {
    checkClosed()
  }

  override def setNCharacterStream(parameterIndex: Int, value: Reader, length: Long): Unit = {
    checkClosed()
  }

  override def setNClob(parameterIndex: Int, value: NClob): Unit = {
    checkClosed()
  }

  override def setClob(parameterIndex: Int, reader: Reader, length: Long): Unit = {
    checkClosed()
  }

  override def setBlob(parameterIndex: Int, inputStream: InputStream, length: Long): Unit = {
    checkClosed()
  }

  override def setNClob(parameterIndex: Int, reader: Reader, length: Long): Unit = {
    checkClosed()
  }

  override def setSQLXML(parameterIndex: Int, xmlObject: SQLXML): Unit = {
    checkClosed()
  }

  override def setObject(parameterIndex: Int, x: Any, targetSqlType: Int, scaleOrLength: Int): Unit = {
    setObject(parameterIndex, x)
  }

  override def setAsciiStream(parameterIndex: Int, x: InputStream, length: Long): Unit = {
    checkClosed()
  }

  override def setBinaryStream(parameterIndex: Int, x: InputStream, length: Long): Unit = {
    checkClosed()
  }

  override def setCharacterStream(parameterIndex: Int, reader: Reader, length: Long): Unit = {
    checkClosed()
  }

  override def setAsciiStream(parameterIndex: Int, x: InputStream): Unit = {
    checkClosed()
  }

  override def setBinaryStream(parameterIndex: Int, x: InputStream): Unit = {
    checkClosed()
  }

  override def setCharacterStream(parameterIndex: Int, reader: Reader): Unit = {
    checkClosed()
  }

  override def setNCharacterStream(parameterIndex: Int, value: Reader): Unit = {
    checkClosed()
  }

  override def setClob(parameterIndex: Int, reader: Reader): Unit = {
    checkClosed()
  }

  override def setBlob(parameterIndex: Int, inputStream: InputStream): Unit = {
    checkClosed()
  }

  override def setNClob(parameterIndex: Int, reader: Reader): Unit = {
    checkClosed()
  }

  override def executeUpdate(sql: String): Int = {
    checkClosed()
    val updateResponse = executeQuery(sql)
    updateResponse.next()
    val updateCount = updateResponse.getInt("matchedCount") + updateResponse.getInt("deletedCount") + updateResponse.getInt("insertedCount")
    _lastUpdateCount = updateCount
    updateCount
  }

  override def getMaxFieldSize: Int = {
    checkClosed()
    0
  }

  override def setMaxFieldSize(max: Int): Unit = {
    checkClosed()
  }

  override def getMaxRows: Int = {
    sqlFeatureNotSupported()
  }

  override def setMaxRows(max: Int): Unit = {
    checkClosed()
  }

  override def setEscapeProcessing(enable: Boolean): Unit = {
    checkClosed()
  }

  override def getQueryTimeout: Int = _queryTimeout

  override def setQueryTimeout(seconds: Int): Unit = _queryTimeout = seconds

  override def cancel(): Unit = {
    sqlFeatureNotSupported("cancel not supported at MongoDb Driver")
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
    _lastResultSet
  }

  override def getUpdateCount: Int = {
    checkClosed()
    _lastUpdateCount
  }

  override def getMoreResults: Boolean = {
    checkClosed()
    false
  }

  override def setFetchDirection(direction: Int): Unit = {
    sqlFeatureNotSupported()
  }

  override def getFetchDirection: Int = {
    checkClosed()
    ResultSet.FETCH_FORWARD
  }

  override def setFetchSize(rows: Int): Unit = {}

  override def getFetchSize: Int = {
    -1
  }

  override def getResultSetConcurrency: Int = {
    sqlFeatureNotSupported()
  }

  override def getResultSetType: Int = {
    checkClosed()
    ResultSet.TYPE_FORWARD_ONLY
  }

  override def addBatch(sql: String): Unit = {
    checkClosed()
  }

  override def clearBatch(): Unit = {
    checkClosed()
  }

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
    executeUpdate(sql)
  }

  override def executeUpdate(sql: String, columnIndexes: Array[Int]): Int = {
    executeUpdate(sql)
  }

  override def executeUpdate(sql: String, columnNames: Array[String]): Int = {
    executeUpdate(sql)
  }

  override def execute(sql: String, autoGeneratedKeys: Int): Boolean = {
    execute(sql)
  }

  override def execute(sql: String, columnIndexes: Array[Int]): Boolean = {
    execute(sql)
  }

  override def execute(sql: String, columnNames: Array[String]): Boolean = {
    execute(sql)
  }

  override def getResultSetHoldability: Int = {
    checkClosed()
    0
  }

  override def setPoolable(poolable: Boolean): Unit = {
    checkClosed()
    0
  }

  override def isPoolable: Boolean = {
    checkClosed()
    false
  }

  override def closeOnCompletion(): Unit = {
    checkClosed()
  }

  override def isCloseOnCompletion: Boolean = {
    checkClosed()
    false
  }
// todo
  override def unwrap[T](iface: Class[T]): T = null.asInstanceOf[T]

  override def isWrapperFor(iface: Class[_]): Boolean = false

  override def registerOutParameter(parameterIndex: Int, sqlType: Int): Unit = ???

  override def registerOutParameter(parameterIndex: Int, sqlType: Int, scale: Int): Unit = ???

  override def wasNull(): Boolean = ???

  override def getString(parameterIndex: Int): String = parameters.get(parameterIndex).orNull

  override def getBoolean(parameterIndex: Int): Boolean = parameters.get(parameterIndex).flatMap(_.toBooleanOption).getOrElse(false)

  override def getByte(parameterIndex: Int): Byte = parameters.get(parameterIndex).flatMap(_.toByteOption).getOrElse(0)

  override def getShort(parameterIndex: Int): Short = parameters.get(parameterIndex).flatMap(_.toShortOption).getOrElse(0)

  override def getInt(parameterIndex: Int): Int = parameters.get(parameterIndex).flatMap(_.toIntOption).getOrElse(0)

  override def getLong(parameterIndex: Int): Long = parameters.get(parameterIndex).flatMap(_.toLongOption).getOrElse(0)

  override def getFloat(parameterIndex: Int): Float = parameters.get(parameterIndex).flatMap(_.toFloatOption).getOrElse(0.0.toFloat)

  override def getDouble(parameterIndex: Int): Double = parameters.get(parameterIndex).flatMap(_.toDoubleOption).getOrElse(0.0)

  override def getBigDecimal(parameterIndex: Int, scale: Int): java.math.BigDecimal = getBigDecimal(parameterIndex)

  override def getBytes(parameterIndex: Int): Array[Byte] = ???

  override def getDate(parameterIndex: Int): Date = ???

  override def getTime(parameterIndex: Int): Time = ???

  override def getTimestamp(parameterIndex: Int): Timestamp = ???

  override def getObject(parameterIndex: Int): AnyRef = ???

  override def getBigDecimal(parameterIndex: Int): java.math.BigDecimal = parameters.get(parameterIndex).flatMap(_.toDoubleOption).map(new java.math.BigDecimal(_)).orNull

  override def getObject(parameterIndex: Int, map: util.Map[String, Class[_]]): AnyRef = ???

  override def getRef(parameterIndex: Int): Ref = ???

  override def getBlob(parameterIndex: Int): Blob = ???

  override def getClob(parameterIndex: Int): Clob = ???

  override def getArray(parameterIndex: Int): sql.Array = ???

  override def getDate(parameterIndex: Int, cal: Calendar): Date = ???

  override def getTime(parameterIndex: Int, cal: Calendar): Time = ???

  override def getTimestamp(parameterIndex: Int, cal: Calendar): Timestamp = ???

  override def registerOutParameter(parameterIndex: Int, sqlType: Int, typeName: String): Unit = ???

  override def registerOutParameter(parameterName: String, sqlType: Int): Unit = ???

  override def registerOutParameter(parameterName: String, sqlType: Int, scale: Int): Unit = ???

  override def registerOutParameter(parameterName: String, sqlType: Int, typeName: String): Unit = ???

  override def getURL(parameterIndex: Int): URL = ???

  override def setURL(parameterName: String, `val`: URL): Unit = ???

  override def setNull(parameterName: String, sqlType: Int): Unit = ???

  override def setBoolean(parameterName: String, x: Boolean): Unit = ???

  override def setByte(parameterName: String, x: Byte): Unit = ???

  override def setShort(parameterName: String, x: Short): Unit = ???

  override def setInt(parameterName: String, x: Int): Unit = ???

  override def setLong(parameterName: String, x: Long): Unit = ???

  override def setFloat(parameterName: String, x: Float): Unit = ???

  override def setDouble(parameterName: String, x: Double): Unit = ???

  override def setBigDecimal(parameterName: String, x: java.math.BigDecimal): Unit = ???

  override def setString(parameterName: String, x: String): Unit = ???

  override def setBytes(parameterName: String, x: Array[Byte]): Unit = ???

  override def setDate(parameterName: String, x: Date): Unit = ???

  override def setTime(parameterName: String, x: Time): Unit = ???

  override def setTimestamp(parameterName: String, x: Timestamp): Unit = ???

  override def setAsciiStream(parameterName: String, x: InputStream, length: Int): Unit = ???

  override def setBinaryStream(parameterName: String, x: InputStream, length: Int): Unit = ???

  override def setObject(parameterName: String, x: Any, targetSqlType: Int, scale: Int): Unit = ???

  override def setObject(parameterName: String, x: Any, targetSqlType: Int): Unit = ???

  override def setObject(parameterName: String, x: Any): Unit = ???

  override def setCharacterStream(parameterName: String, reader: Reader, length: Int): Unit = ???

  override def setDate(parameterName: String, x: Date, cal: Calendar): Unit = ???

  override def setTime(parameterName: String, x: Time, cal: Calendar): Unit = ???

  override def setTimestamp(parameterName: String, x: Timestamp, cal: Calendar): Unit = ???

  override def setNull(parameterName: String, sqlType: Int, typeName: String): Unit = ???

  override def getString(parameterName: String): String = ???

  override def getBoolean(parameterName: String): Boolean = ???

  override def getByte(parameterName: String): Byte = ???

  override def getShort(parameterName: String): Short = ???

  override def getInt(parameterName: String): Int = ???

  override def getLong(parameterName: String): Long = ???

  override def getFloat(parameterName: String): Float = ???

  override def getDouble(parameterName: String): Double = ???

  override def getBytes(parameterName: String): Array[Byte] = ???

  override def getDate(parameterName: String): Date = ???

  override def getTime(parameterName: String): Time = ???

  override def getTimestamp(parameterName: String): Timestamp = ???

  override def getObject(parameterName: String): AnyRef = ???

  override def getBigDecimal(parameterName: String): java.math.BigDecimal = ???

  override def getObject(parameterName: String, map: util.Map[String, Class[_]]): AnyRef = ???

  override def getRef(parameterName: String): Ref = ???

  override def getBlob(parameterName: String): Blob = ???

  override def getClob(parameterName: String): Clob = ???

  override def getArray(parameterName: String): sql.Array = ???

  override def getDate(parameterName: String, cal: Calendar): Date = ???

  override def getTime(parameterName: String, cal: Calendar): Time = ???

  override def getTimestamp(parameterName: String, cal: Calendar): Timestamp = ???

  override def getURL(parameterName: String): URL = ???

  override def getRowId(parameterIndex: Int): RowId = ???

  override def getRowId(parameterName: String): RowId = ???

  override def setRowId(parameterName: String, x: RowId): Unit = ???

  override def setNString(parameterName: String, value: String): Unit = ???

  override def setNCharacterStream(parameterName: String, value: Reader, length: Long): Unit = ???

  override def setNClob(parameterName: String, value: NClob): Unit = ???

  override def setClob(parameterName: String, reader: Reader, length: Long): Unit = ???

  override def setBlob(parameterName: String, inputStream: InputStream, length: Long): Unit = ???

  override def setNClob(parameterName: String, reader: Reader, length: Long): Unit = ???

  override def getNClob(parameterIndex: Int): NClob = ???

  override def getNClob(parameterName: String): NClob = ???

  override def setSQLXML(parameterName: String, xmlObject: SQLXML): Unit = ???

  override def getSQLXML(parameterIndex: Int): SQLXML = ???

  override def getSQLXML(parameterName: String): SQLXML = ???

  override def getNString(parameterIndex: Int): String = ???

  override def getNString(parameterName: String): String = ???

  override def getNCharacterStream(parameterIndex: Int): Reader = ???

  override def getNCharacterStream(parameterName: String): Reader = ???

  override def getCharacterStream(parameterIndex: Int): Reader = ???

  override def getCharacterStream(parameterName: String): Reader = ???

  override def setBlob(parameterName: String, x: Blob): Unit = ???

  override def setClob(parameterName: String, x: Clob): Unit = ???

  override def setAsciiStream(parameterName: String, x: InputStream, length: Long): Unit = ???

  override def setBinaryStream(parameterName: String, x: InputStream, length: Long): Unit = ???

  override def setCharacterStream(parameterName: String, reader: Reader, length: Long): Unit = ???

  override def setAsciiStream(parameterName: String, x: InputStream): Unit = ???

  override def setBinaryStream(parameterName: String, x: InputStream): Unit = ???

  override def setCharacterStream(parameterName: String, reader: Reader): Unit = ???

  override def setNCharacterStream(parameterName: String, value: Reader): Unit = ???

  override def setClob(parameterName: String, reader: Reader): Unit = ???

  override def setBlob(parameterName: String, inputStream: InputStream): Unit = ???

  override def setNClob(parameterName: String, reader: Reader): Unit = ???

  override def getObject[T](parameterIndex: Int, `type`: Class[T]): T = ???

  override def getObject[T](parameterName: String, `type`: Class[T]): T = ???
}
