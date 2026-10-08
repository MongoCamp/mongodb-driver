package dev.mongocamp.driver.mongodb.jdbc

import java.sql.Date
import java.sql.Time
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

object SqlDateTimeConverter {
  private val TimeEpochDate = LocalDate.of(1970, 1, 1)

  def zoneId(cal: Calendar): ZoneId = Option(cal).map(_.getTimeZone.toZoneId).getOrElse(ZoneId.systemDefault())

  def toSqlDate(instant: Instant, zoneId: ZoneId): Date = Date.valueOf(instant.atZone(zoneId).toLocalDate)

  def toSqlTime(instant: Instant, zoneId: ZoneId): Time = new Time(epochMilliOnTimeEpochDate(instant.atZone(zoneId).toLocalTime, ZoneId.systemDefault()))

  def dateToInstant(date: Date, zoneId: ZoneId): Instant = date.toLocalDate.atStartOfDay(zoneId).toInstant

  def timeToInstant(time: Time, zoneId: ZoneId): Instant = {
    val localTime = Instant.ofEpochMilli(time.getTime).atZone(ZoneId.systemDefault()).toLocalTime
    Instant.ofEpochMilli(epochMilliOnTimeEpochDate(localTime, zoneId))
  }

  private def epochMilliOnTimeEpochDate(localTime: LocalTime, zoneId: ZoneId): Long = TimeEpochDate.atTime(localTime).atZone(zoneId).toInstant.toEpochMilli
}
