package dev.mongocamp.driver.mongodb.jdbc

import com.mongodb.event.CommandListener
import com.mongodb.event.ConnectionPoolListener
import dev.mongocamp.driver.mongodb.bson.BsonConverter
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultApplicationName
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultAuthenticationDatabaseName
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultHost
import dev.mongocamp.driver.mongodb.database.MongoConfig.DefaultPort
import dev.mongocamp.driver.mongodb.database.MongoPoolOptions
import dev.mongocamp.driver.mongodb.jdbc.statement.MongoPreparedStatement
import dev.mongocamp.driver.mongodb.json._
import dev.mongocamp.driver.mongodb.json.JsonConverter
import java.sql
import java.sql.Blob
import java.sql.CallableStatement
import java.sql.Clob
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.NClob
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.SQLWarning
import java.sql.SQLXML
import java.sql.Savepoint
import java.sql.Statement
import java.sql.Struct
import java.util
import java.util.concurrent.Executor
import java.util.Properties
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.MongoClientSettings
import org.mongodb.scala.ServerAddress
import scala.jdk.CollectionConverters._

class MongoJdbcConnection(databaseProvider: DatabaseProvider) extends Connection with MongoJdbcCloseable {
  private var _isReadOnly = false

  def getDatabaseProvider: DatabaseProvider = databaseProvider

  override def createStatement(): Statement = MongoPreparedStatement(this)

  override def prepareStatement(sql: String): PreparedStatement = {
    new MongoPreparedStatement(this, sql)
  }

  override def prepareCall(sql: String): CallableStatement = {
    checkClosed()
    createMongoStatement(Some(sql))
  }

  override def nativeSQL(sql: String): String = {
    checkClosed()
    // todo: return debug string
    sql
  }

  override def setAutoCommit(autoCommit: Boolean): Unit = {
    checkClosed()
  }

  override def getAutoCommit: Boolean = {
    checkClosed()
    true
  }

  override def commit(): Unit = {
    checkClosed()
  }

  override def rollback(): Unit = {
    checkClosed()
  }

  override def close(): Unit = {
    super.close()
    databaseProvider.client.close()
  }

  override def getMetaData: DatabaseMetaData = new MongoDatabaseMetaData(this)

  override def setReadOnly(readOnly: Boolean): Unit = {
    checkClosed()
    _isReadOnly = readOnly
  }

  override def isReadOnly: Boolean = _isReadOnly

  override def setCatalog(catalog: String): Unit = {
    checkClosed()
  }

  override def getCatalog: String = null

  override def setTransactionIsolation(level: Int): Unit = {
    sqlFeatureNotSupported()
  }

  override def getTransactionIsolation: Int = {
    checkClosed()
    Connection.TRANSACTION_NONE
  }

  override def getWarnings: SQLWarning = {
    checkClosed()
    null
  }

  override def clearWarnings(): Unit = {
    checkClosed()
  }

  def createMongoStatement(sqlOption: Option[String] = None): MongoPreparedStatement = {
    checkClosed()
    val stmt = statement.MongoPreparedStatement(this)
    sqlOption.foreach(stmt.setSql)
    stmt
  }

  override def createStatement(resultSetType: Int, resultSetConcurrency: Int): Statement = {
    checkClosed()
    createMongoStatement()
  }

  override def prepareStatement(sql: String, resultSetType: Int, resultSetConcurrency: Int): PreparedStatement = {
    checkClosed()
    createMongoStatement(Some(sql))
  }

  override def prepareCall(sql: String, resultSetType: Int, resultSetConcurrency: Int): CallableStatement = {
    checkClosed()
    createMongoStatement(Some(sql))
  }

  override def getTypeMap: util.Map[String, Class[_]] = {
    checkClosed()
    null
  }

  override def setTypeMap(map: util.Map[String, Class[_]]): Unit = {
    checkClosed()
  }

  override def setHoldability(holdability: Int): Unit = {
    checkClosed()
  }

  override def getHoldability: Int = {
    checkClosed()
    0
  }

  override def setSavepoint(): Savepoint = {
    checkClosed()
    null
  }

  override def setSavepoint(name: String): Savepoint = {
    checkClosed()
    null
  }

  override def rollback(savepoint: Savepoint): Unit = {
    checkClosed()
  }

  override def releaseSavepoint(savepoint: Savepoint): Unit = {
    checkClosed()
  }

  override def createStatement(resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): Statement = {
    createMongoStatement()
  }

  override def prepareStatement(sql: String, resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): PreparedStatement = {
    createMongoStatement(Option(sql))
  }

  override def prepareCall(sql: String, resultSetType: Int, resultSetConcurrency: Int, resultSetHoldability: Int): CallableStatement = {
    checkClosed()
    createMongoStatement(Some(sql))
  }

  override def prepareStatement(sql: String, autoGeneratedKeys: Int): PreparedStatement = {
    createMongoStatement(Option(sql))
  }

  override def prepareStatement(sql: String, columnIndexes: Array[Int]): PreparedStatement = {
    createMongoStatement(Option(sql))
  }

  override def prepareStatement(sql: String, columnNames: Array[String]): PreparedStatement = {
    createMongoStatement(Option(sql))
  }

  override def createClob(): Clob = {
    throw sqlFeatureNotSupported()
  }

  override def createBlob(): Blob = {
    throw sqlFeatureNotSupported()
  }

  override def createNClob(): NClob = {
    throw sqlFeatureNotSupported()
  }

  override def createSQLXML(): SQLXML = {
    throw sqlFeatureNotSupported()
  }

  override def isValid(timeout: Int): Boolean = {
    checkClosed()
    true
  }

  override def setClientInfo(name: String, value: String): Unit = {
    checkClosed()
    if ("ApplicationName".equalsIgnoreCase(name) || "appName".equalsIgnoreCase(name) || "name".equalsIgnoreCase(name)) {
      if (value != null) {
        databaseProvider.closeClient()
        databaseProvider.config.applicationName = value
      }
    }
  }

  override def setClientInfo(properties: Properties): Unit = {
    properties.asScala.foreach(
      entry => setClientInfo(entry._1, entry._2)
    )
  }

  override def getClientInfo(name: String): String = {
    checkClosed()
    if ("ApplicationName".equalsIgnoreCase(name) || "appName".equalsIgnoreCase(name) || "name".equalsIgnoreCase(name)) {
      databaseProvider.config.applicationName
    }
    else {
      null
    }
  }

  override def getClientInfo: Properties = {
    val properties = new Properties()
    properties.setProperty("ApplicationName", databaseProvider.config.applicationName)
    case class MongoConfigHelper(
      database: String,
      host: String = DefaultHost,
      port: Int = DefaultPort,
      var applicationName: String = DefaultApplicationName,
      userName: Option[String] = None,
      password: Option[String] = None,
      authDatabase: String = DefaultAuthenticationDatabaseName,
      poolOptions: MongoPoolOptions = MongoPoolOptions(),
      compressors: List[String] = List.empty,
      connectionPoolListener: List[String] = List.empty,
      commandListener: List[String] = List.empty,
      customClientSettings: Option[String] = None,
      serverAddressList: List[String] = List.empty
    )
    val logConfig = Option(databaseProvider.config)
      .map(
        c =>
          MongoConfigHelper(
            c.database,
            c.host,
            c.port,
            c.applicationName,
            c.userName,
            c.password,
            c.authDatabase,
            c.poolOptions,
            c.compressors,
            c.connectionPoolListener.map(_.toString),
            c.commandListener.map(_.toString),
            c.customClientSettings.map(_.toString),
            c.serverAddressList.map(_.toString)
          )
      )
      .get
    val document = Document(new JsonConverter().toJson(logConfig))
    BsonConverter
      .asMap(document)
      .foreach(
        entry => {
          if (entry._2 != null) {
            properties.setProperty(entry._1, entry._2.toString)
          }
        }
      )
    properties
  }

  override def createArrayOf(typeName: String, elements: Array[AnyRef]): sql.Array = {
    throw sqlFeatureNotSupported()
  }

  override def createStruct(typeName: String, attributes: Array[AnyRef]): Struct = {
    throw sqlFeatureNotSupported()
  }

  override def setSchema(schema: String): Unit = {
    checkClosed()
    databaseProvider.setDefaultDatabaseName(schema)
  }

  override def getSchema: String = {
    checkClosed()
    databaseProvider.DefaultDatabaseName
  }

  override def abort(executor: Executor): Unit = {
    checkClosed()
  }

  override def setNetworkTimeout(executor: Executor, milliseconds: Int): Unit = {
    checkClosed()
  }

  override def getNetworkTimeout: Int = {
    checkClosed()
    0
  }

  override def unwrap[T](iface: Class[T]): T = {
    checkClosed()
    null.asInstanceOf[T]
  }

  override def isWrapperFor(iface: Class[_]): Boolean = {
    checkClosed()
    false
  }

}
