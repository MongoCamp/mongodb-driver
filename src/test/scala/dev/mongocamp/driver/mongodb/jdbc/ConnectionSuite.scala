package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.jdbc.statement.MongoPreparedStatement
import java.sql.Connection
import java.sql.SQLFeatureNotSupportedException
import java.sql.Savepoint
import java.util.concurrent.Executor
import java.util.Properties

class ConnectionSuite extends BaseJdbcSuite {

  test("getDatabaseProvider should return the database provider") {
    val driver = new MongoJdbcDriver()
    val connectionUrl =
      "jdbc:mongodb://localhost:27017/mongocamp-unit-test?retryWrites=true&loadBalanced=false&serverSelectionTimeoutMS=5000&connectTimeoutMS=10000"
    val propertiesInfo = driver.getPropertyInfo(connectionUrl, new Properties())
    assertEquals(propertiesInfo.length, 5)
  }

  test("getDatabaseProvider should return the database provider") {
    assertEquals(connection.asInstanceOf[MongoJdbcConnection].getDatabaseProvider.collections().results().isEmpty, false)
  }

  test("createStatement should return a MongoPreparedStatement") {
    assert(connection.createStatement().isInstanceOf[MongoPreparedStatement])
    assert(connection.createStatement(0, 0).isInstanceOf[MongoPreparedStatement])
    assert(connection.createStatement(0, 0, 0).isInstanceOf[MongoPreparedStatement])
  }

  test("prepareStatement should return a MongoPreparedStatement") {
    assert(connection.prepareStatement("SELECT * FROM people").isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareStatement("SELECT * FROM people", 0).isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareStatement("SELECT * FROM people", 0, 0).isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareStatement("SELECT * FROM people", 0, 0, 0).isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareStatement("SELECT * FROM people", Array[Int]()).isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareStatement("SELECT * FROM people", Array[String]()).isInstanceOf[MongoPreparedStatement])
  }

  test("prepareCall should return a MongoPreparedStatement") {
    assert(connection.prepareCall("SELECT * FROM people").isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareCall("SELECT * FROM people", 0, 0).isInstanceOf[MongoPreparedStatement])
    assert(connection.prepareCall("SELECT * FROM people", 0, 0, 0).isInstanceOf[MongoPreparedStatement])
  }

  test("nativeSQL should return the same SQL string") {
    val sql = "SELECT * FROM people"
    assertEquals(connection.nativeSQL(sql), sql)
  }

  test("setAutoCommit should not throw an exception") {
    connection.setAutoCommit(true)
  }

  test("getAutoCommit should return true") {
    assert(connection.getAutoCommit)
  }

  test("commit should not throw an exception") {
    connection.commit()
  }

  test("rollback should not throw an exception") {
    connection.rollback()
  }

  test("getMetaData should return MongoDatabaseMetaData") {
    assert(connection.getMetaData.isInstanceOf[MongoDatabaseMetaData])
  }

  test("setReadOnly should set the connection to read-only") {
    connection.setReadOnly(true)
    assert(connection.isReadOnly)
  }

  test("setCatalog should not throw an exception") {
    connection.setCatalog("testCatalog")
  }

  test("getCatalog should return null") {
    assertEquals(connection.getCatalog, null)
  }

  test("intercept not implemented sql features") {
    intercept[SQLFeatureNotSupportedException](connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED))
    intercept[SQLFeatureNotSupportedException](connection.createNClob())
    intercept[SQLFeatureNotSupportedException](connection.createBlob())
    intercept[SQLFeatureNotSupportedException](connection.createClob())
    intercept[SQLFeatureNotSupportedException](connection.createSQLXML())
    intercept[SQLFeatureNotSupportedException](connection.createStruct("", null))
    intercept[SQLFeatureNotSupportedException](connection.createArrayOf("typeName: String", null))
  }

  test("getTransactionIsolation should return TRANSACTION_NONE") {
    assertEquals(connection.getTransactionIsolation, Connection.TRANSACTION_NONE)
  }

  test("getWarnings should return null") {
    assertEquals(connection.getWarnings, null)
  }

  test("clearWarnings should not throw an exception") {
    connection.clearWarnings()
  }

  test("getTypeMap should return null") {
    assertEquals(connection.getTypeMap, null)
  }

  test("setTypeMap should not throw an exception") {
    connection.setTypeMap(new java.util.HashMap[String, Class[_]]())
  }

  test("setHoldability should not throw an exception") {
    connection.setHoldability(0)
  }

  test("getHoldability should return 0") {
    assertEquals(connection.getHoldability, 0)
  }

  test("setSavepoint should return null") {
    assertEquals(connection.setSavepoint(), null)
  }

  test("setSavepoint with name should return null") {
    assertEquals(connection.setSavepoint("savepoint"), null)
  }

  test("rollback with savepoint should not throw an exception") {
    connection.rollback(null.asInstanceOf[Savepoint])
  }

  test("releaseSavepoint should not throw an exception") {
    connection.releaseSavepoint(null.asInstanceOf[Savepoint])
  }

  test("isValid should return true") {
    assert(connection.isValid(0))
  }

  test("setClientInfo with name and value should not throw an exception") {
    connection.setClientInfo("ApplicationName", "testApp")
  }

  test("setClientInfo with properties should not throw an exception") {
    val properties = new Properties()
    properties.setProperty("ApplicationName", "testApp")
    connection.setClientInfo(properties)
  }

  test("getClientInfo with name should return the application name") {
    connection.setClientInfo("ApplicationName", "testApp")
    assertEquals(connection.getClientInfo("ApplicationName"), "testApp")
  }

  test("getClientInfo should return properties with application name") {
    connection.setClientInfo("ApplicationName", "testApp")
    val properties = connection.getClientInfo
    assertEquals(properties.getProperty("ApplicationName"), "testApp")
  }

  test("getSchema should return the default database name") {
    assertEquals(connection.getSchema, "mongocamp-unit-test")
  }

  test("setSchema should not throw an exception") {
    connection.setSchema("testSchema")
  }

  test("abort should not throw an exception") {
    connection.abort(null.asInstanceOf[Executor])
  }

  test("setNetworkTimeout should not throw an exception") {
    connection.setNetworkTimeout(null.asInstanceOf[Executor], 0)
  }

  test("getNetworkTimeout should return 0") {
    assertEquals(connection.getNetworkTimeout, 0)
  }

  test("unwrap should return null") {
    assertEquals(connection.unwrap(classOf[Connection]), null)
  }

  test("isWrapperFor should return false") {
    assert(!connection.isWrapperFor(classOf[Connection]))
  }

  test("close should close the connection") {
    connection.close()
    assertEquals(connection.isClosed, true)
  }
}
