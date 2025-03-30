package dev.mongocamp.driver.mongodb.jdbc

import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException

trait MongoJdbcCloseable extends AutoCloseable {

  protected def checkClosed(): Unit = {
    if (closed) {
      throw new SQLException("Closed " + this.getClass.getSimpleName)
    }
  }

  private var closed: Boolean = false

  override def close(): Unit = {
    checkClosed()
    closed = true
  }

  def isClosed: Boolean = closed

  def sqlFeatureNotSupported[A <: Any](message: String): A = {
    sqlFeatureNotSupported(Option(message).filter(_.trim.nonEmpty))
  }

  def sqlFeatureNotSupported[A <: Any](message: Option[String] = None): A = {
    checkClosed()
    if (message.nonEmpty) {
      throw new SQLFeatureNotSupportedException(message.get)
    }
    throw new SQLFeatureNotSupportedException()
  }

}
