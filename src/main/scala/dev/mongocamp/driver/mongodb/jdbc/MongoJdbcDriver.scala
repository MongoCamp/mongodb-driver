package dev.mongocamp.driver.mongodb.jdbc

import com.vdurmont.semver4j.Semver
import dev.mongocamp.driver.mongodb.BuildInfo
import dev.mongocamp.driver.mongodb.database.{ DatabaseProvider, MongoConfig }
import org.mongodb.scala.{ ConnectionString, ServerAddress }

import java.sql.{ Connection, DriverPropertyInfo }
import java.util.Properties
import java.util.logging.Logger
import scala.jdk.CollectionConverters.CollectionHasAsScala

class MongoJdbcDriver extends java.sql.Driver {

  private lazy val semVer = new Semver(BuildInfo.version)

  /** Connect to the database using a URL like : jdbc:mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]] The
    * URL excepting the jdbc: prefix is passed as it is to the MongoDb native Java driver.
    */
  override def connect(url: String, info: Properties): Connection = {
    if (url == null || !acceptsURL(url)) {
      return null
    }

    val connectionUrl = url.replaceFirst("^jdbc:", "")
    val username      = Option(info.getProperty("user")).filter(_.trim.nonEmpty)
    val password      = Option(info.getProperty("password")).filter(_.trim.nonEmpty)

    val string = new ConnectionString(connectionUrl)
    val provider = DatabaseProvider(
      MongoConfig(
        string.getDatabase,
        MongoConfig.DefaultHost,
        MongoConfig.DefaultPort,
        string.getApplicationName,
        username,
        password,
        string.getDatabase,
        serverAddressList = string.getHosts.asScala.toList.map(h => new ServerAddress(h))
      )
    )
    new MongoJdbcConnection(provider)
  }

  override def acceptsURL(url: String): Boolean = {
    val internalUrl = url.replaceFirst("^jdbc:", "")
    internalUrl.startsWith("mongodb://") || internalUrl.startsWith("mongodb+srv://")
  }

  override def getPropertyInfo(url: String, info: Properties): Array[DriverPropertyInfo] = ???

  override def getMajorVersion: Int = semVer.getMajor

  override def getMinorVersion: Int = semVer.getMinor

  override def jdbcCompliant(): Boolean = true

  override def getParentLogger: Logger = null
}
