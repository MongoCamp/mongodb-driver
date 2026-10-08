package dev.mongocamp.driver.mongodb.jdbc

import java.sql.Date
import java.sql.Time
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

/** Converts the instants stored in MongoDB to the JDBC date types and back. java.sql.Date and java.sql.Time are normalized in the default time zone as required
  * by their definition, the time zone of the date or time value is given by a calendar or the default time zone.
  */
object SqlDateTimeConverter {
  private val TimeEpochDate = LocalDate.of(1970, 1, 1)

  def zoneId(cal: Calendar): ZoneId = Option(cal).map(_.getTimeZone.toZoneId).getOrElse(ZoneId.systemDefault())

  // the date of the instant in the time zone, normalized to midnight as required by java.sql.Date
  def toSqlDate(instant: Instant, zoneId: ZoneId): Date = Date.valueOf(instant.atZone(zoneId).toLocalDate)

  // the time of the instant in the time zone on 1970-01-01 as required by java.sql.Time
  def toSqlTime(instant: Instant, zoneId: ZoneId): Time = new Time(epochMilliOnTimeEpochDate(instant.atZone(zoneId).toLocalTime, ZoneId.systemDefault()))

  // midnight of the date in the time zone
  def dateToInstant(date: Date, zoneId: ZoneId): Instant = date.toLocalDate.atStartOfDay(zoneId).toInstant

  // the time on 1970-01-01 in the time zone
  def timeToInstant(time: Time, zoneId: ZoneId): Instant = {
    val localTime = Instant.ofEpochMilli(time.getTime).atZone(ZoneId.systemDefault()).toLocalTime
    Instant.ofEpochMilli(epochMilliOnTimeEpochDate(localTime, zoneId))
  }

  private def epochMilliOnTimeEpochDate(localTime: LocalTime, zoneId: ZoneId): Long = TimeEpochDate.atTime(localTime).atZone(zoneId).toInstant.toEpochMilli
}
