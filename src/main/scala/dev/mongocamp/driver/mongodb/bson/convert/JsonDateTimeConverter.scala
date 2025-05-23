package dev.mongocamp.driver.mongodb.bson.convert

import com.typesafe.scalalogging.LazyLogging
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.TimeZone
import org.bson.json.Converter
import org.bson.json.StrictJsonWriter

object JsonDateTimeConverter {
  val Converter = new JsonDateTimeConverter

  val UTC: TimeZone = TimeZone.getTimeZone("UTC")

  val DateFormat: SimpleDateFormat = {
    val f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    f.setTimeZone(UTC)
    f
  }
}

class JsonDateTimeConverter(dateFormat: DateFormat = JsonDateTimeConverter.DateFormat) extends Converter[java.lang.Long] with LazyLogging {
  override def convert(value: java.lang.Long, writer: StrictJsonWriter): Unit =
    try {
      val s = dateFormat.format(value)
      writer.writeString(s)
    }
    catch {
      case e: Exception =>
        logger.error(String.format("Failed to convert value %d to JSON date", value), e)
    }
}
