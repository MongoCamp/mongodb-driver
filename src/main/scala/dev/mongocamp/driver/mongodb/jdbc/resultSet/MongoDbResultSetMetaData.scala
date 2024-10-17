package dev.mongocamp.driver.mongodb.jdbc.resultSet

import dev.mongocamp.driver.mongodb.MongoDAO
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonBoolean, BsonInt32, BsonInt64, BsonNumber, BsonString}
import dev.mongocamp.driver.mongodb._

import java.sql.{ResultSetMetaData, SQLException}

class MongoDbResultSetMetaData extends ResultSetMetaData {
  private var document: Document                = _
  private var collectionDao: MongoDAO[Document] = _

  def this(dao: MongoDAO[Document]) = {
    this()
    val row: Document = extractDocumentFromDataList(dao.findAggregated(List(Map("$sample" -> Map("size" -> 500)))).resultList())
    this.document = row
    this.collectionDao = dao
  }

  def this(dao: MongoDAO[Document], document: Document) = {
    this()
    this.document = document
    this.collectionDao = dao
  }

  def this(dao: MongoDAO[Document], data: List[Document]) = {
    this()
    val row: Document = extractDocumentFromDataList(data)
    this.document = row
    this.collectionDao = dao
  }

  private def extractDocumentFromDataList(data: List[Document]) = {
    var row = data.headOption.getOrElse(throw new SQLException("No data in ResultSet")).copy()
    val distinctKeys = data.flatMap(_.keys).distinct
    val missingKeys = distinctKeys.diff(row.keys.toSeq)
    missingKeys.foreach(key => {
      data
        .find(_.get(key).nonEmpty)
        .map(doc => row = row.updated(key, doc.get(key).get))
    })
    row
  }

  override def getColumnCount: Int = document.size

  override def isAutoIncrement(column: Int): Boolean = false

  override def isCaseSensitive(column: Int): Boolean = true

  override def isSearchable(column: Int): Boolean = true

  override def isCurrency(column: Int): Boolean = false

  override def isNullable(column: Int): Int = ResultSetMetaData.columnNullable

  override def isSigned(column: Int): Boolean = false

  override def getColumnDisplaySize(column: Int): Int = Int.MaxValue

  override def getColumnLabel(column: Int): String = document.keys.toList(column - 1)

  override def getColumnName(column: Int): String = getColumnLabel(column)

  override def getSchemaName(column: Int): String = collectionDao.databaseName

  override def getPrecision(column: Int): Int = 0

  override def getScale(column: Int): Int = 0

  override def getTableName(column: Int): String = collectionDao.name

  override def getCatalogName(column: Int): String = collectionDao.name

  override def getColumnType(column: Int): Int = {
    document.values.toList(column - 1) match {
      case _: BsonInt32   => java.sql.Types.INTEGER
      case _: BsonInt64   => java.sql.Types.BIGINT
      case _: BsonNumber  => java.sql.Types.DOUBLE
      case _: BsonString  => java.sql.Types.VARCHAR
      case _: BsonBoolean => java.sql.Types.BOOLEAN
      case _: Document    => java.sql.Types.STRUCT
      case _              => java.sql.Types.NULL
    }
  }

  override def getColumnTypeName(column: Int): String = {
    getColumnType(column) match {
      case java.sql.Types.INTEGER => "INTEGER"
      case java.sql.Types.BIGINT  => "BIGINT"
      case java.sql.Types.DOUBLE  => "DOUBLE"
      case java.sql.Types.VARCHAR => "VARCHAR"
      case java.sql.Types.BOOLEAN => "BOOLEAN"
      case java.sql.Types.STRUCT  => "STRUCT"
      case _                      => "NULL"
    }
  }

  override def isReadOnly(column: Int): Boolean = false

  override def isWritable(column: Int): Boolean = true

  override def isDefinitelyWritable(column: Int): Boolean = true

  override def getColumnClassName(column: Int): String = {
    getColumnType(column) match {
      case java.sql.Types.INTEGER => classOf[java.lang.Integer].getName
      case java.sql.Types.BIGINT  => classOf[java.lang.Long].getName
      case java.sql.Types.DOUBLE  => classOf[java.lang.Double].getName
      case java.sql.Types.VARCHAR => classOf[java.lang.String].getName
      case java.sql.Types.BOOLEAN => classOf[java.lang.Boolean].getName
      case java.sql.Types.STRUCT  => classOf[java.lang.Object].getName
      case _                      => classOf[java.lang.String].getName
    }
  }

  override def unwrap[T](iface: Class[T]): T = null.asInstanceOf[T]

  override def isWrapperFor(iface: Class[_]): Boolean = false

  def getColumnIndex(columnLabel: String): Int = {
    document.keys.toList.indexOf(columnLabel)
  }
}
