package dev.mongocamp.driver.mongodb.jdbc

import java.sql.Date
import java.sql.Time
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

class SqlDateTimeConverterSuite extends munit.FunSuite {

  private val instant = Instant.parse("2021-01-01T00:15:30.123Z")
  private val utc     = ZoneId.of("UTC")
  private val tokyo   = ZoneId.of("Asia/Tokyo")

  private def withDefaultTimeZone(zoneId: String)(f: => Unit): Unit = {
    val defaultTimeZone = TimeZone.getDefault
    try {
      TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
      f
    }
    finally TimeZone.setDefault(defaultTimeZone)
  }

  private def localDateOf(date: java.util.Date): LocalDate = Instant.ofEpochMilli(date.getTime).atZone(ZoneId.systemDefault()).toLocalDate

  test("zoneId uses the time zone of the calendar or the default time zone") {
    assertEquals(SqlDateTimeConverter.zoneId(Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))), tokyo)
    assertEquals(SqlDateTimeConverter.zoneId(null), ZoneId.systemDefault())
  }

  test("toSqlDate returns the date in the time zone normalized to midnight") {
    withDefaultTimeZone("America/New_York") {
      assertEquals(SqlDateTimeConverter.toSqlDate(instant, utc), Date.valueOf("2021-01-01"))
      assertEquals(SqlDateTimeConverter.toSqlDate(instant, ZoneId.systemDefault()), Date.valueOf("2020-12-31"))
      assertEquals(SqlDateTimeConverter.toSqlDate(instant, tokyo).toString, "2021-01-01")
    }
  }

  test("toSqlTime returns the time in the time zone on 1970-01-01 with milliseconds") {
    withDefaultTimeZone("America/New_York") {
      val time = SqlDateTimeConverter.toSqlTime(instant, utc)
      assertEquals(time.toString, "00:15:30")
      assertEquals(localDateOf(time), LocalDate.of(1970, 1, 1))
      assertEquals(Instant.ofEpochMilli(time.getTime).atZone(ZoneId.systemDefault()).toLocalTime, LocalTime.of(0, 15, 30, 123000000))
      assertEquals(SqlDateTimeConverter.toSqlTime(instant, tokyo).toString, "09:15:30")
      assertEquals(SqlDateTimeConverter.toSqlTime(instant, ZoneId.systemDefault()).toString, "19:15:30")
    }
  }

  test("dateToInstant returns midnight of the date in the time zone") {
    withDefaultTimeZone("America/New_York") {
      assertEquals(SqlDateTimeConverter.dateToInstant(Date.valueOf("2021-01-01"), utc), Instant.parse("2021-01-01T00:00:00Z"))
      assertEquals(SqlDateTimeConverter.dateToInstant(Date.valueOf("2021-01-01"), tokyo), Instant.parse("2020-12-31T15:00:00Z"))
    }
  }

  test("timeToInstant returns the time on 1970-01-01 in the time zone with milliseconds") {
    withDefaultTimeZone("America/New_York") {
      val time = SqlDateTimeConverter.toSqlTime(instant, utc)
      assertEquals(SqlDateTimeConverter.timeToInstant(time, utc), Instant.parse("1970-01-01T00:15:30.123Z"))
      assertEquals(SqlDateTimeConverter.timeToInstant(Time.valueOf("10:15:30"), tokyo), Instant.parse("1970-01-01T01:15:30Z"))
    }
  }

  test("date and time roundtrip in the time zone") {
    withDefaultTimeZone("Asia/Tokyo") {
      val date = Date.valueOf("2021-01-01")
      assertEquals(SqlDateTimeConverter.toSqlDate(SqlDateTimeConverter.dateToInstant(date, utc), utc), date)
      val time = Time.valueOf("23:59:59")
      assertEquals(SqlDateTimeConverter.toSqlTime(SqlDateTimeConverter.timeToInstant(time, utc), utc), time)
    }
  }

}
