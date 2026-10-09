package dev.mongocamp.driver.mongodb.json

import io.circe.Codec
import io.circe.Decoder
import io.circe.Json
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

class CirceSchemaSuite extends munit.FunSuite {

  private val millis     = 1709198130123L
  private val instantIso = "2024-02-29T09:15:30.123Z"

  private def assertFormat[A](format: Codec[A], value: A, json: String): Unit = {
    assertEquals(format.apply(value), Json.fromString(json))
    assertEquals(format.decodeJson(Json.fromString(json)), Right(value))
  }

  private def assertDecode[A](format: Decoder[A], json: String, value: A): Unit = {
    assertEquals(format.decodeJson(Json.fromString(json)), Right(value))
  }

  private def startOfDayIso(localDate: java.time.LocalDate): String = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant.toString

  test("java.time.LocalDate format") {
    val localDate = java.time.LocalDate.of(2024, 2, 29)
    assertFormat(JavaLocalDateFormat, localDate, "2024-02-29")
    assertDecode(JavaLocalDateFormat, startOfDayIso(localDate), localDate)
    assertDecode(JavaLocalDateFormat, "2024-02-29T10:15:30", localDate)
  }

  test("java.time.LocalDateTime format") {
    val localDateTime = java.time.LocalDateTime.of(2024, 2, 29, 10, 15, 30, 123000000)
    assertFormat(JavaLocalDateTimeFormat, localDateTime, "2024-02-29T10:15:30.123")
    assertDecode(JavaLocalDateTimeFormat, localDateTime.atZone(ZoneId.systemDefault()).toInstant.toString, localDateTime)
    assertDecode(JavaLocalDateTimeFormat, "2024-02-29", java.time.LocalDateTime.of(2024, 2, 29, 0, 0))
  }

  test("java.time formats do not depend on joda default time zone") {
    val defaultTimeZone = java.util.TimeZone.getDefault
    try {
      java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Pacific/Kiritimati"))
      val localDate = java.time.LocalDate.of(2024, 2, 29)
      assertDecode(JavaLocalDateFormat, "2024-02-29", localDate)
      assertDecode(JavaLocalDateFormat, startOfDayIso(localDate), localDate)
      assertDecode(JavaLocalDateTimeFormat, "2024-02-29T10:15:30", java.time.LocalDateTime.of(2024, 2, 29, 10, 15, 30))
    }
    finally java.util.TimeZone.setDefault(defaultTimeZone)
  }

  test("java.sql.Date format") {
    val sqlDate = java.sql.Date.valueOf("2024-02-29")
    assertFormat(SqlDateFormat, sqlDate, startOfDayIso(java.time.LocalDate.of(2024, 2, 29)))
  }

  test("java.sql.Timestamp format") {
    assertFormat(SqlTimestampFormat, new java.sql.Timestamp(millis), instantIso)
  }

  test("joda Instant format") {
    assertFormat(JodaInstantFormat, new org.joda.time.Instant(millis), instantIso)
  }

  test("joda MutableDateTime format") {
    assertFormat(JodaMutableDateTimeFormat, new org.joda.time.MutableDateTime(millis), instantIso)
  }

  test("joda LocalDate format") {
    val localDate = new org.joda.time.LocalDate(2024, 2, 29)
    assertFormat(JodaLocalDateFormat, localDate, "2024-02-29")
    assertDecode(JodaLocalDateFormat, Instant.ofEpochMilli(localDate.toDateTimeAtStartOfDay.getMillis).toString, localDate)
  }

  test("joda LocalDateTime format") {
    val localDateTime = new org.joda.time.LocalDateTime(2024, 2, 29, 10, 15, 30, 123)
    assertFormat(JodaLocalDateTimeFormat, localDateTime, "2024-02-29T10:15:30.123")
    assertDecode(JodaLocalDateTimeFormat, Instant.ofEpochMilli(localDateTime.toDateTime.getMillis).toString, localDateTime)
  }

  test("joda LocalTime format") {
    assertFormat(JodaLocalTimeFormat, new org.joda.time.LocalTime(10, 15, 30, 123), "10:15:30.123")
  }

  test("joda Duration format") {
    val duration = org.joda.time.Duration.standardHours(25)
    assertFormat(JodaDurationFormat, duration, "90000000 milliseconds")
    assertDecode(JodaDurationFormat, "PT90000S", duration)
    assertDecode(JodaDurationFormat, "25 hours", duration)
  }

  test("joda Period format") {
    assertFormat(JodaPeriodFormat, new org.joda.time.Period(1, 2, 0, 3, 4, 5, 6, 7), "P1Y2M3DT4H5M6.007S")
  }

  test("joda DateTimeZone format") {
    assertFormat(JodaDateTimeZoneFormat, org.joda.time.DateTimeZone.forID("Europe/Berlin"), "Europe/Berlin")
  }

  test("joda YearMonth format") {
    assertFormat(JodaYearMonthFormat, new org.joda.time.YearMonth(2024, 2), "2024-02")
  }

  test("joda MonthDay format") {
    assertFormat(JodaMonthDayFormat, new org.joda.time.MonthDay(2, 29), "--02-29")
  }

  test("string formats encode null as json null") {
    assertEquals(JodaLocalDateFormat.apply(null: org.joda.time.LocalDate), Json.Null)
    assertEquals(JavaLocalDateFormat.apply(null: java.time.LocalDate), Json.Null)
  }

  test("string formats fail on invalid input") {
    assert(JodaLocalDateFormat.decodeJson(Json.fromString("no date")).isLeft)
    assert(JavaLocalDateFormat.decodeJson(Json.fromString("no date")).isLeft)
    assert(JodaDurationFormat.decodeJson(Json.fromString("no duration")).isLeft)
    assert(JodaDateTimeZoneFormat.decodeJson(Json.fromString("no zone")).isLeft)
    assert(JodaLocalDateFormat.decodeJson(Json.fromInt(42)).isLeft)
  }

  test("encodeAnyToJson converts UUID and date types") {
    val uuid = UUID.fromString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c")
    assertEquals(encodeAnyToJson(uuid), Json.fromString("1c9b6e2a-3f4d-4c5e-8a7b-9d0e1f2a3b4c"))
    assertEquals(encodeAnyToJson(new java.util.Date(millis)), Json.fromString(instantIso))
    assertEquals(encodeAnyToJson(new java.sql.Date(millis)), Json.fromString(instantIso))
    assertEquals(encodeAnyToJson(new java.sql.Timestamp(millis)), Json.fromString(instantIso))
  }

  test("encodeAnyToJson converts java.time types") {
    assertEquals(encodeAnyToJson(java.time.LocalDate.of(2024, 2, 29)), Json.fromString("2024-02-29"))
    assertEquals(encodeAnyToJson(Instant.ofEpochMilli(millis)), Json.fromString(instantIso))
    assertEquals(encodeAnyToJson(java.time.Duration.ofMinutes(90)), Json.fromString("PT1H30M"))
    assertEquals(encodeAnyToJson(java.time.Period.of(1, 2, 3)), Json.fromString("P1Y2M3D"))
    assertEquals(encodeAnyToJson(ZoneId.of("Europe/Berlin")), Json.fromString("Europe/Berlin"))
    assertEquals(encodeAnyToJson(java.time.ZoneOffset.ofHours(-3)), Json.fromString("-03:00"))
  }

  test("encodeAnyToJson converts joda types") {
    assertEquals(encodeAnyToJson(new org.joda.time.Instant(millis)), Json.fromString(instantIso))
    assertEquals(encodeAnyToJson(new org.joda.time.LocalDate(2024, 2, 29)), Json.fromString("2024-02-29"))
    assertEquals(encodeAnyToJson(new org.joda.time.Period(1, 2, 0, 3, 0, 0, 0, 0)), Json.fromString("P1Y2M3D"))
    assertEquals(encodeAnyToJson(org.joda.time.Duration.standardHours(25)), Json.fromString("PT90000S"))
    assertEquals(encodeAnyToJson(org.joda.time.DateTimeZone.forID("Europe/Berlin")), Json.fromString("Europe/Berlin"))
  }

}
