package dev.mongocamp.driver.mongodb.jdbc.resultSet

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.jdbc.MongoJdbcCloseable
import dev.mongocamp.driver.mongodb.json._
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.{ BsonArray, BsonBoolean, BsonDateTime, BsonDouble, BsonInt32, BsonInt64, BsonNull, BsonNumber, BsonObjectId, BsonString }
import org.mongodb.scala.documentToUntypedDocument

import java.io.{ InputStream, Reader }
import java.net.{ URI, URL }
import java.nio.charset.StandardCharsets
import java.sql.{ Blob, Clob, Date, NClob, Ref, ResultSet, ResultSetMetaData, RowId, SQLException, SQLWarning, SQLXML, Statement, Time, Timestamp }
import java.util.Calendar
import java.{ sql, util }
import javax.sql.rowset.serial.SerialBlob
import scala.util.Try

class MongoDbResultSet(collectionDao: MongoDAO[Document], data: List[Document], queryTimeOut: Int, keySet: List[String] = List.empty)
    extends ResultSet
    with MongoJdbcCloseable {
  private var currentRow: Document = _
  private var index: Int           = 0

  private lazy val metaData = new MongoDbResultSetMetaData(collectionDao, data, keySet)

  def getDocument: Document = currentRow

  override def next(): Boolean = {
    checkClosed()
    if (data == null || data.isEmpty) {
      false
    }
    else {
      if (index == 0 || (currentRow != null && index < data.size)) {
        currentRow = data(index)
        index += 1
        true
      }
      else {
        currentRow = null
        false
      }
    }
  }

  override def wasNull(): Boolean = {
    checkClosed()
    false
  }

  override def getString(columnIndex: Int): String = {
    checkClosed()
    currentRow.getString(metaData.getColumnName(columnIndex))
  }

  override def getBoolean(columnIndex: Int): Boolean = {
    checkClosed()
    currentRow.getBoolean(metaData.getColumnName(columnIndex))
  }

  override def getByte(columnIndex: Int): Byte = {
    checkClosed()
    getInt(columnIndex).toByte
  }

  override def getShort(columnIndex: Int): Short = {
    checkClosed()
    getInt(columnIndex).toShort
  }

  override def getInt(columnIndex: Int): Int = {
    checkClosed()
    getLong(columnIndex).toInt
  }

  override def getLong(columnIndex: Int): Long = {
    checkClosed()
    getDouble(columnIndex).toLong
  }

  override def getFloat(columnIndex: Int): Float = {
    checkClosed()
    getDouble(columnIndex).toFloat
  }

  override def getDouble(columnIndex: Int): Double = {
    checkClosed()
    val value = currentRow.getValue(metaData.getColumnName(columnIndex))
    value match {
      case b: BsonInt32  => b.doubleValue()
      case b: BsonInt64  => b.doubleValue()
      case b: BsonDouble => b.doubleValue()
      case _             => Option(value).flatMap(v => Try(v.toString.toDouble).toOption).getOrElse(0)
    }
  }

  override def getBigDecimal(columnIndex: Int, scale: Int): java.math.BigDecimal = {
    checkClosed()
    new java.math.BigDecimal(getDouble(columnIndex)).setScale(scale)
  }

  override def getBytes(columnIndex: Int): Array[Byte] = {
    checkClosed()
    getString(columnIndex).replace("[", "").replace("]", "").split(",").filterNot(s => s.trim.isEmpty).map(_.trim.toByte)
  }

  override def getDate(columnIndex: Int): Date = {
    checkClosed()
    val javaDate = currentRow.getDateValue(metaData.getColumnName(columnIndex))
    new Date(javaDate.getTime)
  }

  override def getTime(columnIndex: Int): Time = {
    checkClosed()
    val javaDate = currentRow.getDateValue(metaData.getColumnName(columnIndex))
    new Time(javaDate.getTime)
  }

  override def getTimestamp(columnIndex: Int): Timestamp = {
    checkClosed()
    val javaDate = currentRow.getDateValue(metaData.getColumnName(columnIndex))
    new Timestamp(javaDate.getTime)
  }

  override def getAsciiStream(columnIndex: Int): InputStream = {
    checkClosed()
    null
  }

  override def getUnicodeStream(columnIndex: Int): InputStream = {
    checkClosed()
    null
  }

  override def getBinaryStream(columnIndex: Int): InputStream = {
    checkClosed()
    null
  }

  override def getString(columnLabel: String): String = {
    checkClosed()
    currentRow.get(columnLabel) match {
      case Some(value) =>
        value match {
          case v: BsonString   => v.getValue
          case v: BsonObjectId => v.asObjectId().getValue.toHexString
          case _               => Option(BsonConverter.fromBson(value)).map(_.toString).orNull
        }
      case None => ""
    }
  }

  override def getBoolean(columnLabel: String): Boolean = {
    checkClosed()
    currentRow.getBoolean(columnLabel)
  }

  override def getByte(columnLabel: String): Byte = {
    checkClosed()
    getInt(columnLabel).toByte
  }

  override def getShort(columnLabel: String): Short = {
    checkClosed()
    getInt(columnLabel).toShort
  }

  override def getInt(columnLabel: String): Int = {
    checkClosed()
    getDouble(columnLabel).toInt
  }

  override def getLong(columnLabel: String): Long = {
    checkClosed()
    getDouble(columnLabel).toLong
  }

  override def getFloat(columnLabel: String): Float = {
    checkClosed()
    getDouble(columnLabel).toFloat
  }

  override def getDouble(columnLabel: String): Double = {
    checkClosed()
    val value = currentRow.getValue(columnLabel)
    value match {
      case b: BsonInt32  => b.doubleValue()
      case b: BsonInt64  => b.doubleValue()
      case b: BsonDouble => b.doubleValue()
      case _             => Option(value).flatMap(v => Try(v.toString.toDouble).toOption).getOrElse(0)
    }
  }

  override def getBigDecimal(columnLabel: String, scale: Int): java.math.BigDecimal = {
    checkClosed()
    new java.math.BigDecimal(getDouble(columnLabel)).setScale(scale)
  }

  override def getBytes(columnLabel: String): Array[Byte] = {
    checkClosed()
    getString(columnLabel).replace("[", "").replace("]", "").split(",").filterNot(s => s.trim.isEmpty).map(_.trim.toByte)
  }

  override def getDate(columnLabel: String): Date = {
    checkClosed()
    val javaDate = currentRow.getDateValue(columnLabel)
    new Date(javaDate.getTime)
  }

  override def getTime(columnLabel: String): Time = {
    checkClosed()
    val javaDate = currentRow.getDateValue(columnLabel)
    new Time(javaDate.getTime)
  }

  override def getTimestamp(columnLabel: String): Timestamp = {
    checkClosed()
    val javaDate = currentRow.getDateValue(columnLabel)
    new Timestamp(javaDate.getTime)
  }

  override def getAsciiStream(columnLabel: String): InputStream = {
    checkClosed()
    null
  }

  override def getUnicodeStream(columnLabel: String): InputStream = {
    checkClosed()
    null
  }

  override def getBinaryStream(columnLabel: String): InputStream = {
    checkClosed()
    null
  }

  override def getWarnings: SQLWarning = {
    checkClosed()
    null
  }

  override def clearWarnings(): Unit = {
    checkClosed()
  }

  override def getCursorName: String = {
    checkClosed()
    null
  }

  override def getMetaData: ResultSetMetaData = {
    checkClosed()
    new MongoDbResultSetMetaData(collectionDao, data)
  }

  override def getObject(columnIndex: Int): AnyRef = {
    checkClosed()
    currentRow.get(metaData.getColumnName(columnIndex)) match {
      case Some(value) => BsonConverter.fromBson(value).asInstanceOf[AnyRef]
      case None        => null
    }
  }

  override def getObject(columnLabel: String): AnyRef = {
    checkClosed()
    currentRow.get(columnLabel) match {
      case Some(value) => BsonConverter.fromBson(value).asInstanceOf[AnyRef]
      case None        => null
    }
  }

  override def findColumn(columnLabel: String): Int = {
    checkClosed()
    metaData.getColumnIndex(columnLabel)
  }

  override def getCharacterStream(columnIndex: Int): Reader = {
    checkClosed()
    null
  }

  override def getCharacterStream(columnLabel: String): Reader = {
    checkClosed()
    null
  }

  override def getBigDecimal(columnIndex: Int): java.math.BigDecimal = {
    checkClosed()
    new java.math.BigDecimal(getDouble(columnIndex))
  }

  override def getBigDecimal(columnLabel: String): java.math.BigDecimal = {
    checkClosed()
    new java.math.BigDecimal(getDouble(columnLabel))
  }

  override def isBeforeFirst: Boolean = {
    checkClosed()
    index == 0
  }

  override def isAfterLast: Boolean = {
    checkClosed()
    index >= data.size
  }

  override def isFirst: Boolean = {
    checkClosed()
    index == 1
  }

  override def isLast: Boolean = {
    checkClosed()
    index == data.size
  }

  override def beforeFirst(): Unit = {
    checkClosed()
  }

  override def afterLast(): Unit = {
    checkClosed()
  }

  override def first(): Boolean = isBeforeFirst

  override def last(): Boolean = isLast

  override def getRow: Int = {
    checkClosed()
    if (currentRow == null) {
      0
    }
    else {
      index
    }
  }

  override def absolute(row: Int): Boolean = {
    checkClosed()
    false
  }

  override def relative(rows: Int): Boolean = {
    checkClosed()
    false
  }

  override def previous(): Boolean = {
    checkClosed()
    false
  }

  override def setFetchDirection(direction: Int): Unit = sqlFeatureNotSupported()

  override def getFetchDirection: Int = {
    checkClosed()
    ResultSet.FETCH_FORWARD
  }

  override def setFetchSize(rows: Int): Unit = {
    checkClosed()
  }

  override def getFetchSize: Int = {
    checkClosed()
    1
  }

  override def getType: Int = {
    checkClosed()
    ResultSet.TYPE_FORWARD_ONLY
  }

  override def getConcurrency: Int = sqlFeatureNotSupported()

  override def rowUpdated(): Boolean = {
    checkClosed()
    false
  }

  override def rowInserted(): Boolean = {
    checkClosed()
    false
  }

  override def rowDeleted(): Boolean = {
    checkClosed()
    false
  }

  override def updateNull(columnIndex: Int): Unit = {
    updateObject(columnIndex, null)
  }

  override def updateNull(columnLabel: String): Unit = {
    updateObject(columnLabel, null)
  }

  override def updateBoolean(columnIndex: Int, x: Boolean): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateBoolean(columnLabel: String, x: Boolean): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateByte(columnIndex: Int, x: Byte): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateByte(columnLabel: String, x: Byte): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateShort(columnIndex: Int, x: Short): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateShort(columnLabel: String, x: Short): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateInt(columnIndex: Int, x: Int): Unit = {
    updateObject(columnIndex, x)

  }

  override def updateInt(columnLabel: String, x: Int): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateLong(columnIndex: Int, x: Long): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateLong(columnLabel: String, x: Long): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateFloat(columnIndex: Int, x: Float): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateFloat(columnLabel: String, x: Float): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateDouble(columnIndex: Int, x: Double): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateDouble(columnLabel: String, x: Double): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateBigDecimal(columnIndex: Int, x: java.math.BigDecimal): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateBigDecimal(columnLabel: String, x: java.math.BigDecimal): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateString(columnIndex: Int, x: String): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateString(columnLabel: String, x: String): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateBytes(columnIndex: Int, x: Array[Byte]): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateBytes(columnLabel: String, x: Array[Byte]): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateDate(columnIndex: Int, x: Date): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateDate(columnLabel: String, x: Date): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateTime(columnIndex: Int, x: Time): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateTime(columnLabel: String, x: Time): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateTimestamp(columnIndex: Int, x: Timestamp): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateTimestamp(columnLabel: String, x: Timestamp): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateAsciiStream(columnIndex: Int, x: InputStream, length: Int): Unit = sqlFeatureNotSupported()

  override def updateAsciiStream(columnLabel: String, x: InputStream, length: Int): Unit = sqlFeatureNotSupported()

  override def updateBinaryStream(columnIndex: Int, x: InputStream, length: Int): Unit = sqlFeatureNotSupported()

  override def updateBinaryStream(columnLabel: String, x: InputStream, length: Int): Unit = sqlFeatureNotSupported()

  override def updateCharacterStream(columnIndex: Int, x: Reader, length: Int): Unit = sqlFeatureNotSupported()

  override def updateCharacterStream(columnLabel: String, reader: Reader, length: Int): Unit = sqlFeatureNotSupported()

  override def updateObject(columnIndex: Int, x: Any, scaleOrLength: Int): Unit = {
    updateObject(columnIndex, x)
  }

  override def updateObject(columnLabel: String, x: Any, scaleOrLength: Int): Unit = {
    updateObject(columnLabel, x)
  }

  override def updateObject(columnIndex: Int, x: Any): Unit = {
    updateObject(metaData.getColumnName(columnIndex), x)
  }

  override def updateObject(columnLabel: String, x: Any): Unit = {
    checkClosed()
    currentRow = currentRow.updated(columnLabel, BsonConverter.toBson(x))
    data.updated(index, currentRow)
  }

  override def insertRow(): Unit = {
    checkClosed()
    collectionDao.insertOne(currentRow).resultOption(queryTimeOut)
  }

  override def updateRow(): Unit = {
    checkClosed()
    collectionDao.replaceOne(currentRow).resultOption(queryTimeOut)
  }

  override def deleteRow(): Unit = {
    checkClosed()
    collectionDao.deleteOne(currentRow).resultOption(queryTimeOut)
  }

  override def refreshRow(): Unit = {
    checkClosed()
    currentRow.get("_id") match {
      case Some(id) =>
        collectionDao.find(Map("_id" -> id)).resultOption(queryTimeOut) match {
          case Some(document) => currentRow = document
          case None           => throw new SQLException("Row not found")
        }
      case None => throw new SQLException("No _id field in current row")
    }
  }

  override def cancelRowUpdates(): Unit = sqlFeatureNotSupported()

  override def moveToInsertRow(): Unit = sqlFeatureNotSupported()

  override def moveToCurrentRow(): Unit = sqlFeatureNotSupported()

  override def getStatement: Statement = {
    checkClosed()
    null
  }

  override def getObject(columnIndex: Int, map: util.Map[String, Class[_]]): AnyRef = {
    checkClosed()
    if (map == null || map.isEmpty) {
      getObject(columnIndex)
    }
    else {
      sqlFeatureNotSupported()
    }
  }

  override def getObject(columnLabel: String, map: util.Map[String, Class[_]]): AnyRef = {
    checkClosed()
    if (map == null || map.isEmpty) {
      getObject(columnLabel)
    }
    else {
      sqlFeatureNotSupported()
    }
  }

  override def getObject[T](columnIndex: Int, `type`: Class[T]): T = {
    checkClosed()
    val ref = getObject(columnIndex)
    ref match {
      case t: T => t
      case _    => throw new SQLException("Invalid type")
    }
  }

  override def getObject[T](columnLabel: String, `type`: Class[T]): T = {
    checkClosed()
    val ref = getObject(columnLabel)
    ref match {
      case t: T => t
      case _    => throw new SQLException("Invalid type")
    }
  }

  override def getRef(columnIndex: Int): Ref = sqlFeatureNotSupported()

  override def getRef(columnLabel: String): Ref = sqlFeatureNotSupported()

  override def updateRef(columnIndex: Int, x: Ref): Unit = sqlFeatureNotSupported()

  override def updateRef(columnLabel: String, x: Ref): Unit = sqlFeatureNotSupported()

  override def getDate(columnIndex: Int, cal: Calendar): Date = {
    checkClosed()
    val date = getDate(columnIndex)
    convertDateWithCalendar(cal, date)
  }

  override def getDate(columnLabel: String, cal: Calendar): Date = {
    checkClosed()
    val date = getDate(columnLabel)
    convertDateWithCalendar(cal, date)
  }

  override def getTime(columnIndex: Int, cal: Calendar): Time = {
    checkClosed()
    val date = getDate(columnIndex, cal)
    new Time(date.getTime)
  }

  override def getTime(columnLabel: String, cal: Calendar): Time = {
    checkClosed()
    val date = getDate(columnLabel, cal)
    new Time(date.getTime)
  }

  override def getTimestamp(columnIndex: Int, cal: Calendar): Timestamp = {
    checkClosed()
    val date = getDate(columnIndex, cal)
    new Timestamp(date.getTime)
  }

  override def getTimestamp(columnLabel: String, cal: Calendar): Timestamp = {
    checkClosed()
    val date = getDate(columnLabel, cal)
    new Timestamp(date.getTime)
  }

  override def getURL(columnIndex: Int): URL = {
    checkClosed()
    new URI(getString(columnIndex)).toURL
  }

  override def getURL(columnLabel: String): URL = {
    checkClosed()
    new URI(getString(columnLabel)).toURL
  }

  override def getRowId(columnIndex: Int): RowId                = sqlFeatureNotSupported()
  override def getRowId(columnLabel: String): RowId             = sqlFeatureNotSupported()
  override def updateRowId(columnIndex: Int, x: RowId): Unit    = sqlFeatureNotSupported()
  override def updateRowId(columnLabel: String, x: RowId): Unit = sqlFeatureNotSupported()

  override def getHoldability: Int = sqlFeatureNotSupported()

  override def updateNString(columnIndex: Int, nString: String): Unit    = sqlFeatureNotSupported()
  override def updateNString(columnLabel: String, nString: String): Unit = sqlFeatureNotSupported()
  override def getNString(columnIndex: Int): String                      = sqlFeatureNotSupported()
  override def getNString(columnLabel: String): String                   = sqlFeatureNotSupported()

  override def getNClob(columnIndex: Int): NClob                    = sqlFeatureNotSupported()
  override def getNClob(columnLabel: String): NClob                 = sqlFeatureNotSupported()
  override def updateNClob(columnIndex: Int, nClob: NClob): Unit    = sqlFeatureNotSupported()
  override def updateNClob(columnLabel: String, nClob: NClob): Unit = sqlFeatureNotSupported()

  override def getSQLXML(columnIndex: Int): SQLXML                        = sqlFeatureNotSupported()
  override def getSQLXML(columnLabel: String): SQLXML                     = sqlFeatureNotSupported()
  override def updateSQLXML(columnIndex: Int, xmlObject: SQLXML): Unit    = sqlFeatureNotSupported()
  override def updateSQLXML(columnLabel: String, xmlObject: SQLXML): Unit = sqlFeatureNotSupported()

  override def getNCharacterStream(columnIndex: Int): Reader                                   = sqlFeatureNotSupported()
  override def getNCharacterStream(columnLabel: String): Reader                                = sqlFeatureNotSupported()
  override def updateNCharacterStream(columnIndex: Int, x: Reader, length: Long): Unit         = sqlFeatureNotSupported()
  override def updateNCharacterStream(columnLabel: String, reader: Reader, length: Long): Unit = sqlFeatureNotSupported()

  override def updateAsciiStream(columnIndex: Int, x: InputStream, length: Long): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }

  override def updateAsciiStream(columnLabel: String, x: InputStream, length: Long): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def updateAsciiStream(columnIndex: Int, x: InputStream): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }

  override def updateAsciiStream(columnLabel: String, x: InputStream): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def updateBinaryStream(columnIndex: Int, x: InputStream, length: Long): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }

  override def updateBinaryStream(columnLabel: String, x: InputStream, length: Long): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def updateBinaryStream(columnIndex: Int, x: InputStream): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }

  override def updateBinaryStream(columnLabel: String, x: InputStream): Unit = {
    val text = new String(x.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def updateCharacterStream(columnIndex: Int, x: Reader, length: Long): Unit         = sqlFeatureNotSupported()
  override def updateCharacterStream(columnLabel: String, reader: Reader, length: Long): Unit = sqlFeatureNotSupported()
  override def updateCharacterStream(columnIndex: Int, x: Reader): Unit                       = sqlFeatureNotSupported()
  override def updateCharacterStream(columnLabel: String, reader: Reader): Unit               = sqlFeatureNotSupported()

  override def updateNCharacterStream(columnIndex: Int, x: Reader): Unit         = sqlFeatureNotSupported()
  override def updateNCharacterStream(columnLabel: String, reader: Reader): Unit = sqlFeatureNotSupported()

  override def updateBlob(columnIndex: Int, inputStream: InputStream, length: Long): Unit = {
    val text = new String(inputStream.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }
  override def updateBlob(columnLabel: String, inputStream: InputStream, length: Long): Unit = {
    val text = new String(inputStream.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }
  override def updateBlob(columnIndex: Int, inputStream: InputStream): Unit = {
    val text = new String(inputStream.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }
  override def updateBlob(columnLabel: String, inputStream: InputStream): Unit = {
    val text = new String(inputStream.readAllBytes, StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def updateClob(columnIndex: Int, reader: Reader, length: Long): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnIndex, text)
  }

  override def updateNClob(columnIndex: Int, reader: Reader, length: Long): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnIndex, text)
  }

  override def updateNClob(columnLabel: String, reader: Reader, length: Long): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnLabel, text)
  }

  override def updateNClob(columnIndex: Int, reader: Reader): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnIndex, text)
  }

  override def updateNClob(columnLabel: String, reader: Reader): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnLabel, text)
  }

  override def getBlob(columnIndex: Int): Blob = {
    new SerialBlob(getBytes(columnIndex))
  }

  override def getBlob(columnLabel: String): Blob = {
    new SerialBlob(getBytes(columnLabel))
  }

  override def updateBlob(columnIndex: Int, x: Blob): Unit = {
    val text = new String(x.getBinaryStream.readAllBytes(), StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }

  override def updateBlob(columnLabel: String, x: Blob): Unit = {
    val text = new String(x.getBinaryStream.readAllBytes(), StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def getClob(columnIndex: Int): Clob = sqlFeatureNotSupported()

  override def getClob(columnLabel: String): Clob = sqlFeatureNotSupported()

  override def updateClob(columnIndex: Int, x: Clob): Unit = {
    val text = new String(x.getAsciiStream.readAllBytes(), StandardCharsets.UTF_8)
    updateString(columnIndex, text)
  }

  override def updateClob(columnLabel: String, x: Clob): Unit = {
    val text = new String(x.getAsciiStream.readAllBytes(), StandardCharsets.UTF_8)
    updateString(columnLabel, text)
  }

  override def updateClob(columnLabel: String, reader: Reader, length: Long): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnLabel, text)
  }

  override def updateClob(columnIndex: Int, reader: Reader): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnIndex, text)
  }

  override def updateClob(columnLabel: String, reader: Reader): Unit = {
    val text = convertReaderToString(reader)
    updateString(columnLabel, text)
  }

  override def getArray(columnIndex: Int): sql.Array = sqlFeatureNotSupported()

  override def getArray(columnLabel: String): sql.Array = sqlFeatureNotSupported()

  override def updateArray(columnIndex: Int, x: sql.Array): Unit = sqlFeatureNotSupported()

  override def updateArray(columnLabel: String, x: sql.Array): Unit = sqlFeatureNotSupported()

  override def unwrap[T](iface: Class[T]): T = null.asInstanceOf[T]

  override def isWrapperFor(iface: Class[_]): Boolean = false

  private def convertDateWithCalendar(cal: Calendar, date: Date) = {
    if (cal != null) {
      val calDate = cal.getTime
      calDate.setTime(date.getTime)
      new Date(calDate.getTime)
    }
    else {
      date
    }
  }

  private def convertReaderToString(reader: Reader): String = {
    val buffer = new StringBuilder
    var c      = reader.read()
    while (c != -1) {
      buffer.append(c.toChar)
      c = reader.read()
    }
    buffer.toString()
  }

}
