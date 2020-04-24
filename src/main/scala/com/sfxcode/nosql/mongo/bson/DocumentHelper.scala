package com.sfxcode.nosql.mongo.bson

import better.files.{ Scanner, StringSplitter }
import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.Document

object DocumentHelper extends LazyLogging {
  val SplitterDelimeter = ' '
  /*
   iso date bugffix
   */
  def documentFromJsonString(json: String): Option[Document] = {
    var result: Option[Document] = None

    try {
      result = Some(Document(json))
    } catch {
      case e: Exception if e.getMessage.contains("parse string as a date") => {
        logger.debug("parse error - try to replace iso date")
        val scanner = Scanner(json, splitter = StringSplitter.on(SplitterDelimeter))
        var i = 0
        var datePosition = 0
        val resultBuffer = new StringBuffer()

        scanner.foreach { s =>
          i = i + 1
          if ("\"$date\"".equals(s)) {
            datePosition = i + 2
          }
          if (i == datePosition) {
            if (s.length == 30 && (s.charAt(24) == '+' || s.charAt(24) == '-')) {
              resultBuffer.append(s.substring(0, 27))
              resultBuffer.append(":")
              resultBuffer.append(s.substring(27))
            } else {
              resultBuffer.append(s)
            }
          } else {
            resultBuffer.append(s)
          }
          resultBuffer.append(SplitterDelimeter)
        }
        result = Some(Document(resultBuffer.toString))
      }
    }
    result
  }
}
