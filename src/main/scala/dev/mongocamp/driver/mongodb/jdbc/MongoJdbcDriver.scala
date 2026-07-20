package dev.mongocamp.driver.mongodb.jdbc

import com.vdurmont.semver4j.Semver
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.database.MongoConfig
import dev.mongocamp.driver.mongodb.BuildInfo
import java.sql.Connection
import java.sql.DriverPropertyInfo
import java.util.logging.Logger
import java.util.Properties
import org.mongodb.scala.ConnectionString
import org.mongodb.scala.ServerAddress
import scala.jdk.CollectionConverters._

class MongoJdbcDriver extends java.sql.Driver {
  private val propertyInfoHelper = new MongodbJdbcDriverPropertyInfoHelper()

  private lazy val semVer = new Semver(BuildInfo.version)

  /** Connect to the database using a URL like : jdbc:mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]] The
    * URL excepting the jdbc: prefix is passed as it is to the MongoDb native Java driver.
    */
  override def connect(url: String, info: Properties): Connection = {
    if (url == null || !acceptsURL(url)) {
      return null
    }

    val connectionUrl = url.replaceFirst("^jdbc:", "")
    val username      = Option(info.getProperty(MongodbJdbcDriverPropertyInfoHelper.AuthUser)).filter(_.trim.nonEmpty)
    val password      = Option(info.getProperty(MongodbJdbcDriverPropertyInfoHelper.AuthPassword)).filter(_.trim.nonEmpty)

    val connectionString = new ConnectionString(connectionUrl)
    val database = Option(connectionString.getDatabase).getOrElse(Option(info.getProperty(MongodbJdbcDriverPropertyInfoHelper.Database)).getOrElse("admin"))
    val authDb   = Option(info.getProperty(MongodbJdbcDriverPropertyInfoHelper.AuthDatabase)).getOrElse("admin")

    val hostList: List[ServerAddress] = connectionString.getHosts.asScala.toList.map(MongoConfig.extractServerAddressFromString)

    val provider = DatabaseProvider(
      MongoConfig(
        database,
        hostList.headOption.map(_.getHost).getOrElse(MongoConfig.DefaultHost),
        hostList.headOption.map(_.getPort).getOrElse(MongoConfig.DefaultPort),
        Option(connectionString.getApplicationName).filter(_.trim.nonEmpty).getOrElse(info.getProperty(MongodbJdbcDriverPropertyInfoHelper.ApplicationName)),
        username,
        password,
        authDb,
        serverAddressList = hostList
      )
    )
    new MongoJdbcConnection(provider)
  }

  override def acceptsURL(url: String): Boolean = {
    val internalUrl = url.replaceFirst("^jdbc:", "")
    internalUrl.startsWith("mongodb://") || internalUrl.startsWith("mongodb+srv://")
  }

  override def getPropertyInfo(url: String, info: Properties): Array[DriverPropertyInfo] = propertyInfoHelper.getPropertyInfo

  override def getMajorVersion: Int = semVer.getMajor

  override def getMinorVersion: Int = semVer.getMinor

  override def jdbcCompliant(): Boolean = true

  override def getParentLogger: Logger = null
}
