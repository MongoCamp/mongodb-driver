package dev.mongocamp.driver.mongodb.jdbc

import com.mongodb.MongoCredential.createCredential
import com.vdurmont.semver4j.Semver
import dev.mongocamp.driver.mongodb.BuildInfo
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ConnectionString, MongoClient, MongoClientSettings, MongoCredential}

import java.sql.{Connection, DriverPropertyInfo}
import java.util.Properties
import java.util.logging.Logger

class MongoJdbcDriver extends java.sql.Driver {

  private lazy val semVer = new Semver(BuildInfo.version)


  /**
   * Connect to the database using a URL like :
   * jdbc:mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
   * The URL excepting the jdbc: prefix is passed as it is to the MongoDb native Java driver.
   */
  override def connect(url: String, info: Properties): Connection = {
    if (url == null || !acceptsURL(url)) {
      return null
    }

    val connectionUrl = url.replaceFirst("^jdbc:", "")
    val username = info.getProperty("user")
    val password = info.getProperty("password")

    val builder = MongoClientSettings
      .builder()
      .applyConnectionString(new ConnectionString(connectionUrl))
      .codecRegistry(DEFAULT_CODEC_REGISTRY)

    if (!username.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
      val credential: MongoCredential = createCredential(username, "admin", password.toCharArray)
      builder.credential(credential).build()
    }

    val client: MongoClient = MongoClient(builder.build())
    new MongoJdbcConnection(client)
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
