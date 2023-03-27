package dev.mongocamp.driver.mongodb.database
import dev.mongocamp.driver.mongodb._
import org.mongodb.scala.bson.Document

case class CompactResult(bytesFreed: Long)

object CompactResult {
  def apply(document: Document): Option[CompactResult] = {
    if (document.getLongValue("ok") == 1) {
      Some(
        CompactResult(
          document.getLongValue("bytesFreed")
        )
      )
    }
    else {
      None
    }
  }
}
