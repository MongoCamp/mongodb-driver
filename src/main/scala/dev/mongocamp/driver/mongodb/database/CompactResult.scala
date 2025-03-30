package dev.mongocamp.driver.mongodb.database
import dev.mongocamp.driver.mongodb._
import java.util.Date
import org.mongodb.scala.bson.Document

case class CompactResult(collectionName: String, bytesFreed: Long, duration: Long)

object CompactResult {
  def apply(collectionName: String, document: Document, startDate: Date): Option[CompactResult] = {
    if (document.getLongValue("ok") == 1) {
      Some(CompactResult(collectionName, document.getLongValue("bytesFreed"), new Date().getTime - startDate.getTime))
    }
    else {
      None
    }
  }
}
