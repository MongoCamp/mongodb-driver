package dev.mongocamp.driver.mongodb.jdbc

import dev.mongocamp.driver.mongodb.dao.PersonSpecification

import java.sql.{ Connection, DriverManager }
import java.util.Properties

class BaseJdbcSpec extends PersonSpecification {
  var connection : Connection = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    // #region register-driver
    val connectionProps = new Properties()
    val driver          = new MongoJdbcDriver()
    DriverManager.registerDriver(driver)
    connection = DriverManager.getConnection(
      "jdbc:mongodb://localhost:27017/mongocamp-unit-test?retryWrites=true&loadBalanced=false&serverSelectionTimeoutMS=5000&connectTimeoutMS=10000",
      connectionProps
    )
    // #endregion register-driver
  }

}
