package dev.mongocamp.driver.mongodb.jdbc.statement

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb.exception.SqlCommandNotSupportedException
import dev.mongocamp.driver.mongodb.jdbc.resultSet.MongoDbResultSet
import dev.mongocamp.driver.mongodb.jdbc.{ MongoJdbcCloseable, MongoJdbcConnection }
import dev.mongocamp.driver.mongodb.json.JsonConverter
import dev.mongocamp.driver.mongodb.sql.MongoSqlQueryHolder
import dev.mongocamp.driver.mongodb.{ Converter, GenericObservable }
import org.joda.time.DateTime

import java.io.{ InputStream, Reader }
import java.net.URL
import java.sql.{
  Blob,
  CallableStatement,
  Clob,
  Connection,
  Date,
  NClob,
  ParameterMetaData,
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
import java.{ sql, util }
import scala.collection.mutable
import scala.util.Try

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
        case e: java.sql.SQLException =>
          logger.error(e.getMessage, e)
          null
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
        queryHolder.getKeysFromSelect.foreach(key => emptyDocument.put(key, null))
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
      val resultSet = new MongoDbResultSet(collectionName.orNull, response.toList, getQueryTimeout, queryHolder.getKeysFromSelect)
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
    setObject(parameterIndex, x.mkString("[", ",", "]"))
  }

  override def setDate(parameterIndex: Int, x: Date): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'${new DateTime(x).toInstant.toString}'")
  }

  override def setTime(parameterIndex: Int, x: Time): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'${new DateTime(x).toInstant.toString}'")
  }

  override def setTimestamp(parameterIndex: Int, x: Timestamp): Unit = {
    checkClosed()
    setObject(parameterIndex, s"'${new DateTime(x).toInstant.toString}'")
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
    checkClosed()
    setObject(parameterIndex, x)
  }

  override def setObject(parameterIndex: Int, x: Any): Unit = {
    checkClosed()
    x match {
      case null =>
        parameters.put(parameterIndex, "null")
      case d: Date =>
        parameters.put(parameterIndex, s"'${d.toInstant.toString}'")
      case d: DateTime =>
        parameters.put(parameterIndex, s"'${d.toInstant.toString}'")
      case t: Time =>
        parameters.put(parameterIndex, s"'${t.toInstant.toString}'")
      case a: Array[Byte] =>
        parameters.put(parameterIndex, new JsonConverter().toJson(a))
      case a: Iterable[_] =>
        parameters.put(parameterIndex, new JsonConverter().toJson(a))
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
    setString(parameterIndex, x.toString)
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
    val updateCount = updateResponse.getInt("modifiedCount") + updateResponse.getInt("deletedCount") + updateResponse.getInt("insertedCount")
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

  override def unwrap[T](iface: Class[T]): T = {
    checkClosed()
    null.asInstanceOf[T]
  }

  override def isWrapperFor(iface: Class[_]): Boolean = {
    checkClosed()
    false
  }

  override def wasNull(): Boolean = {
    checkClosed()
    false
  }

  def getStringOption(parameterIndex: Int): Option[String] = {
    checkClosed()
    parameters.get(parameterIndex).map(_.replace("'", ""))
  }

  override def getString(parameterIndex: Int): String = {
    getStringOption(parameterIndex).orNull
  }

  override def getBoolean(parameterIndex: Int): Boolean = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toBoolean).toOption).getOrElse(false)
  }

  override def getByte(parameterIndex: Int): Byte = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toByte).toOption).getOrElse(Byte.MinValue)
  }

  override def getShort(parameterIndex: Int): Short = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toShort).toOption).getOrElse(Short.MinValue)
  }

  override def getInt(parameterIndex: Int): Int = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toInt).toOption).getOrElse(Int.MinValue)
  }

  override def getLong(parameterIndex: Int): Long = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toLong).toOption).getOrElse(Long.MinValue)
  }

  override def getFloat(parameterIndex: Int): Float = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toFloat).toOption).getOrElse(Float.MinValue)
  }

  override def getDouble(parameterIndex: Int): Double = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(v.toDouble).toOption).getOrElse(Double.MinValue)
  }

  override def getBigDecimal(parameterIndex: Int, scale: Int): java.math.BigDecimal = getBigDecimal(parameterIndex)

  override def getBytes(parameterIndex: Int): Array[Byte] = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(new JsonConverter().toObject[Array[Byte]](v)).toOption).orNull
  }

  override def getDate(parameterIndex: Int): Date = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(new Date(DateTime.parse(v).getMillis)).toOption).orNull
  }

  override def getTime(parameterIndex: Int): Time = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(new Time(DateTime.parse(v).getMillis)).toOption).orNull
  }

  override def getTimestamp(parameterIndex: Int): Timestamp = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(new Timestamp(DateTime.parse(v).getMillis)).toOption).orNull
  }

  override def getObject(parameterIndex: Int): AnyRef = {
    checkClosed()
    getStringOption(parameterIndex).orNull
  }

  override def getBigDecimal(parameterIndex: Int): java.math.BigDecimal = {
    checkClosed()
    getStringOption(parameterIndex).flatMap(v => Try(new java.math.BigDecimal(v.toDouble)).toOption).orNull
  }

  override def getObject(parameterIndex: Int, map: util.Map[String, Class[_]]): AnyRef = {
    checkClosed()
    getStringOption(parameterIndex).orNull
  }

  override def getRef(parameterIndex: Int): Ref = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getBlob(parameterIndex: Int): Blob = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getClob(parameterIndex: Int): Clob = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getArray(parameterIndex: Int): sql.Array = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getDate(parameterIndex: Int, cal: Calendar): Date = getDate(parameterIndex)

  override def getTime(parameterIndex: Int, cal: Calendar): Time = getTime(parameterIndex)

  override def getTimestamp(parameterIndex: Int, cal: Calendar): Timestamp = getTimestamp(parameterIndex)

  override def registerOutParameter(parameterIndex: Int, sqlType: Int, typeName: String): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def registerOutParameter(parameterName: String, sqlType: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def registerOutParameter(parameterName: String, sqlType: Int, scale: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def registerOutParameter(parameterName: String, sqlType: Int, typeName: String): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def registerOutParameter(parameterIndex: Int, sqlType: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def registerOutParameter(parameterIndex: Int, sqlType: Int, scale: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getURL(parameterIndex: Int): URL = {
    checkClosed()
    Option(getString(parameterIndex))
      .flatMap(v => {
        val urlParser = Try(new java.net.URI(v).toURL)
        urlParser.toOption
      })
      .orNull
  }

  override def getString(parameterName: String): String = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getBoolean(parameterName: String): Boolean = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getByte(parameterName: String): Byte = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getShort(parameterName: String): Short = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getInt(parameterName: String): Int = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getLong(parameterName: String): Long = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getFloat(parameterName: String): Float = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getDouble(parameterName: String): Double = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getBytes(parameterName: String): Array[Byte] = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getDate(parameterName: String): Date = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getTime(parameterName: String): Time = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getTimestamp(parameterName: String): Timestamp = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getObject(parameterName: String): AnyRef = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getBigDecimal(parameterName: String): java.math.BigDecimal = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getObject(parameterName: String, map: util.Map[String, Class[_]]): AnyRef = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getRef(parameterName: String): Ref = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getBlob(parameterName: String): Blob = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getClob(parameterName: String): Clob = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getArray(parameterName: String): sql.Array = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getDate(parameterName: String, cal: Calendar): Date = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getTime(parameterName: String, cal: Calendar): Time = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getTimestamp(parameterName: String, cal: Calendar): Timestamp = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getURL(parameterName: String): URL = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getRowId(parameterIndex: Int): RowId = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getRowId(parameterName: String): RowId = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setRowId(parameterName: String, x: RowId): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNString(parameterName: String, value: String): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNCharacterStream(parameterName: String, value: Reader, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNClob(parameterName: String, value: NClob): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setClob(parameterName: String, reader: Reader, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBlob(parameterName: String, inputStream: InputStream, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNClob(parameterName: String, reader: Reader, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getNClob(parameterIndex: Int): NClob = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getNClob(parameterName: String): NClob = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setSQLXML(parameterName: String, xmlObject: SQLXML): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getSQLXML(parameterIndex: Int): SQLXML = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getSQLXML(parameterName: String): SQLXML = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getNString(parameterIndex: Int): String = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getNString(parameterName: String): String = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getNCharacterStream(parameterIndex: Int): Reader = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getNCharacterStream(parameterName: String): Reader = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getCharacterStream(parameterIndex: Int): Reader = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getCharacterStream(parameterName: String): Reader = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBlob(parameterName: String, x: Blob): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setClob(parameterName: String, x: Clob): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setAsciiStream(parameterName: String, x: InputStream, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBinaryStream(parameterName: String, x: InputStream, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setCharacterStream(parameterName: String, reader: Reader, length: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setAsciiStream(parameterName: String, x: InputStream): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBinaryStream(parameterName: String, x: InputStream): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setCharacterStream(parameterName: String, reader: Reader): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNCharacterStream(parameterName: String, value: Reader): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setClob(parameterName: String, reader: Reader): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBlob(parameterName: String, inputStream: InputStream): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNClob(parameterName: String, reader: Reader): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getObject[T](parameterIndex: Int, `type`: Class[T]): T = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def getObject[T](parameterName: String, `type`: Class[T]): T = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setURL(parameterName: String, `val`: URL): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNull(parameterName: String, sqlType: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBoolean(parameterName: String, x: Boolean): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setByte(parameterName: String, x: Byte): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setShort(parameterName: String, x: Short): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setInt(parameterName: String, x: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setLong(parameterName: String, x: Long): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setFloat(parameterName: String, x: Float): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setDouble(parameterName: String, x: Double): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBigDecimal(parameterName: String, x: java.math.BigDecimal): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setString(parameterName: String, x: String): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBytes(parameterName: String, x: Array[Byte]): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setDate(parameterName: String, x: Date): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setTime(parameterName: String, x: Time): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setTimestamp(parameterName: String, x: Timestamp): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setAsciiStream(parameterName: String, x: InputStream, length: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setBinaryStream(parameterName: String, x: InputStream, length: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setObject(parameterName: String, x: Any, targetSqlType: Int, scale: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setObject(parameterName: String, x: Any, targetSqlType: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setObject(parameterName: String, x: Any): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setCharacterStream(parameterName: String, reader: Reader, length: Int): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setDate(parameterName: String, x: Date, cal: Calendar): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setTime(parameterName: String, x: Time, cal: Calendar): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setTimestamp(parameterName: String, x: Timestamp, cal: Calendar): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

  override def setNull(parameterName: String, sqlType: Int, typeName: String): Unit = {
    checkClosed()
    sqlFeatureNotSupported()
  }

}
