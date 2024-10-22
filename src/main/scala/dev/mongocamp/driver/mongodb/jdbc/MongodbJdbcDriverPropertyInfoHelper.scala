package dev.mongocamp.driver.mongodb.jdbc
import MongodbJdbcDriverPropertyInfoHelper._

object MongodbJdbcDriverPropertyInfoHelper {
  val ApplicationName = "appName"
  val Database        = "database"

  val AuthUser       = "user"
  val AuthPassword   = "password"
  val AuthDatabase   = "auth_database"
  val DefaultAuthDB  = "admin"
  val DefaultAppName = "mongodb-driver"
}

case class MongodbJdbcDriverPropertyInfoHelper() {

  def getPropertyInfo: Array[java.sql.DriverPropertyInfo] = {
    Array(
      createPropertyInfo(AuthUser, null, Some("The username to authenticate")),
      createPropertyInfo(AuthPassword, null, Some("The password to authenticate")),
      createPropertyInfo(AuthDatabase, DefaultAuthDB, Some("The database where user info is stored. (most cases 'admin')")),
      createPropertyInfo(ApplicationName, DefaultAppName, Some("The application name witch is visible in the MongoDB logs.")),
      createPropertyInfo(Database, null, Some("The default database to connect to for the driver"))
    )
  }

  private def createPropertyInfo(key: String, value: String, description: Option[String] = None): java.sql.DriverPropertyInfo = {
    val prop = new java.sql.DriverPropertyInfo(key, value)
    description.foreach(prop.description = _)
    prop
  }
}
