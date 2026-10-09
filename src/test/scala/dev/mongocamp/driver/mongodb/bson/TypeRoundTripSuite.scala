package dev.mongocamp.driver.mongodb.bson

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.Filter
import dev.mongocamp.driver.mongodb.MongoDAO
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import org.bson.UuidRepresentation
import org.mongodb.scala.bson.BsonBinary
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.Document

case class JavaBaseTypes(
  string: String,
  char: Char,
  boolean: Boolean,
  byte: Byte,
  short: Short,
  int: Int,
  long: Long,
  float: Float,
  double: Double,
  bigInt: BigInt,
  bigDecimal: BigDecimal,
  javaInteger: java.lang.Integer,
  javaLong: java.lang.Long,
  javaDouble: java.lang.Double,
  javaBoolean: java.lang.Boolean,
  javaBigInteger: java.math.BigInteger,
  javaBigDecimal: java.math.BigDecimal,
  uuid: UUID,
  optionalUuid: Option[UUID],
  uuidList: List[UUID],
  date: Date,
  sqlDate: java.sql.Date,
  sqlTimestamp: java.sql.Timestamp,
  _id: ObjectId = new ObjectId()
)

case class JavaTimeTypes(
  localDate: java.time.LocalDate,
  localDateTime: java.time.LocalDateTime,
  localTime: java.time.LocalTime,
  instant: java.time.Instant,
  zonedDateTime: java.time.ZonedDateTime,
  offsetDateTime: java.time.OffsetDateTime,
  offsetTime: java.time.OffsetTime,
  duration: java.time.Duration,
  period: java.time.Period,
  year: java.time.Year,
  yearMonth: java.time.YearMonth,
  monthDay: java.time.MonthDay,
  zoneId: java.time.ZoneId,
  zoneOffset: java.time.ZoneOffset,
  _id: ObjectId = new ObjectId()
)

case class JodaTypes(
  dateTime: org.joda.time.DateTime,
  mutableDateTime: org.joda.time.MutableDateTime,
  instant: org.joda.time.Instant,
  localDate: org.joda.time.LocalDate,
  localDateTime: org.joda.time.LocalDateTime,
  localTime: org.joda.time.LocalTime,
  duration: org.joda.time.Duration,
  period: org.joda.time.Period,
  dateTimeZone: org.joda.time.DateTimeZone,
  yearMonth: org.joda.time.YearMonth,
  monthDay: org.joda.time.MonthDay,
  optionalLocalDate: Option[org.joda.time.LocalDate],
  _id: ObjectId = new ObjectId()
)

case class UuidTest(uuid: UUID, _id: ObjectId = new ObjectId())

class TypeRoundTripSuite extends munit.FunSuite {

  object JavaBaseTypesDAO extends MongoDAO[JavaBaseTypes](TestDatabase.provider, "type-roundtrip-java-base")

  object JavaTimeTypesDAO extends MongoDAO[JavaTimeTypes](TestDatabase.provider, "type-roundtrip-java-time")

  object JodaTypesDAO extends MongoDAO[JodaTypes](TestDatabase.provider, "type-roundtrip-joda")

  object UuidDAO extends MongoDAO[UuidTest](TestDatabase.provider, "type-roundtrip-uuid")

  object UuidDocumentDAO extends MongoDAO[Document](TestDatabase.provider, "type-roundtrip-uuid")

  private val javaBaseTypes = JavaBaseTypes(
    string = "MongoCamp",
    char = 'M',
    boolean = true,
    byte = 12.toByte,
    short = 1234.toShort,
    int = 123456,
    long = 1234567890123L,
    float = 1.1f,
    double = 2.2,
    bigInt = BigInt("1234567890"),
    bigDecimal = BigDecimal("1234.5678"),
    javaInteger = Integer.valueOf(42),
    javaLong = java.lang.Long.valueOf(42L),
    javaDouble = java.lang.Double.valueOf(4.2),
    javaBoolean = java.lang.Boolean.FALSE,
    javaBigInteger = new java.math.BigInteger("9876543210"),
    javaBigDecimal = new java.math.BigDecimal("9876.54321"),
    uuid = UUID.randomUUID(),
    optionalUuid = Some(UUID.randomUUID()),
    uuidList = List(UUID.randomUUID(), UUID.randomUUID()),
    date = new Date(1709198130123L),
    sqlDate = java.sql.Date.valueOf("2024-02-29"),
    sqlTimestamp = new java.sql.Timestamp(1709198130123L)
  )

  private val javaTimeTypes = JavaTimeTypes(
    localDate = java.time.LocalDate.of(2024, 2, 29),
    localDateTime = java.time.LocalDateTime.of(2024, 2, 29, 10, 15, 30, 123000000),
    localTime = java.time.LocalTime.of(10, 15, 30, 123456789),
    instant = java.time.Instant.ofEpochMilli(1709198130123L),
    zonedDateTime = java.time.ZonedDateTime.of(2024, 2, 29, 10, 15, 30, 123000000, ZoneId.of("America/New_York")),
    offsetDateTime = java.time.OffsetDateTime.of(2024, 2, 29, 10, 15, 30, 123000000, java.time.ZoneOffset.ofHours(5)),
    offsetTime = java.time.OffsetTime.of(10, 15, 30, 0, java.time.ZoneOffset.ofHours(2)),
    duration = java.time.Duration.ofMinutes(90),
    period = java.time.Period.of(1, 2, 3),
    year = java.time.Year.of(2024),
    yearMonth = java.time.YearMonth.of(2024, 2),
    monthDay = java.time.MonthDay.of(2, 29),
    zoneId = ZoneId.of("Europe/Berlin"),
    zoneOffset = java.time.ZoneOffset.ofHours(-3)
  )

  private val jodaTypes = JodaTypes(
    dateTime = new org.joda.time.DateTime(2024, 2, 29, 10, 15, 30, 123),
    mutableDateTime = new org.joda.time.MutableDateTime(2024, 2, 29, 10, 15, 30, 123, org.joda.time.DateTimeZone.getDefault),
    instant = new org.joda.time.Instant(1709198130123L),
    localDate = new org.joda.time.LocalDate(2024, 2, 29),
    localDateTime = new org.joda.time.LocalDateTime(2024, 2, 29, 10, 15, 30, 123),
    localTime = new org.joda.time.LocalTime(10, 15, 30, 123),
    duration = org.joda.time.Duration.standardHours(25),
    period = new org.joda.time.Period(1, 2, 0, 3, 4, 5, 6, 7),
    dateTimeZone = org.joda.time.DateTimeZone.forID("Europe/Berlin"),
    yearMonth = new org.joda.time.YearMonth(2024, 2),
    monthDay = new org.joda.time.MonthDay(2, 29),
    optionalLocalDate = Some(new org.joda.time.LocalDate(2023, 12, 24))
  )

  override def beforeEach(context: BeforeEach): Unit = {
    JavaBaseTypesDAO.drop().result()
    JavaTimeTypesDAO.drop().result()
    JodaTypesDAO.drop().result()
    UuidDAO.drop().result()
  }

  test("write and read java and scala base types") {
    JavaBaseTypesDAO.insertOne(javaBaseTypes).result()
    val result = JavaBaseTypesDAO.find("_id", javaBaseTypes._id).result()
    assertEquals(result, javaBaseTypes)
  }

  test("write and read java.time types") {
    JavaTimeTypesDAO.insertOne(javaTimeTypes).result()
    val result = JavaTimeTypesDAO.find("_id", javaTimeTypes._id).result()
    // zoned and offset date times are stored as BSON date, so only the instant survives the roundtrip
    assertEquals(result.zonedDateTime.toInstant, javaTimeTypes.zonedDateTime.toInstant)
    assertEquals(result.offsetDateTime.toInstant, javaTimeTypes.offsetDateTime.toInstant)
    assertEquals(result.copy(zonedDateTime = javaTimeTypes.zonedDateTime, offsetDateTime = javaTimeTypes.offsetDateTime), javaTimeTypes)
  }

  test("write and read joda time types") {
    JodaTypesDAO.insertOne(jodaTypes).result()
    val result = JodaTypesDAO.find("_id", jodaTypes._id).result()
    assertEquals(result, jodaTypes)
  }

  test("store java.util.UUID as string") {
    JavaBaseTypesDAO.insertOne(javaBaseTypes).result()
    val document = JavaBaseTypesDAO.collection.find().result()
    assertEquals(document("uuid").asString().getValue, javaBaseTypes.uuid.toString)
    assertEquals(document("optionalUuid").asString().getValue, javaBaseTypes.optionalUuid.get.toString)
  }

  test("store local dates as date") {
    JodaTypesDAO.insertOne(jodaTypes).result()
    val jodaDocument = JodaTypesDAO.collection.find().result()
    assertEquals(jodaDocument("localDate").asDateTime().getValue, jodaTypes.localDate.toDateTimeAtStartOfDay.getMillis)

    JavaTimeTypesDAO.insertOne(javaTimeTypes).result()
    val javaDocument = JavaTimeTypesDAO.collection.find().result()
    assertEquals(javaDocument("localDate").asDateTime().getValue, javaTimeTypes.localDate.atStartOfDay(ZoneId.systemDefault()).toInstant.toEpochMilli)
  }

  test("query by java.util.UUID and joda LocalDate") {
    JavaBaseTypesDAO.insertOne(javaBaseTypes).result()
    JavaBaseTypesDAO.insertOne(javaBaseTypes.copy(uuid = UUID.randomUUID(), _id = new ObjectId())).result()
    assertEquals(JavaBaseTypesDAO.find("uuid", javaBaseTypes.uuid).resultList().map(_._id), List(javaBaseTypes._id))

    JodaTypesDAO.insertOne(jodaTypes).result()
    JodaTypesDAO.insertOne(jodaTypes.copy(localDate = jodaTypes.localDate.plusDays(1), _id = new ObjectId())).result()
    assertEquals(JodaTypesDAO.find("localDate", jodaTypes.localDate).resultList().map(_._id), List(jodaTypes._id))
  }

  test("query by java.time.LocalDate") {
    JavaTimeTypesDAO.insertOne(javaTimeTypes).result()
    JavaTimeTypesDAO.insertOne(javaTimeTypes.copy(localDate = javaTimeTypes.localDate.plusDays(1), _id = new ObjectId())).result()
    assertEquals(JavaTimeTypesDAO.find("localDate", javaTimeTypes.localDate).resultList().map(_._id), List(javaTimeTypes._id))
  }

  test("query with value filter by list of java.util.UUID") {
    val second = javaBaseTypes.copy(uuid = UUID.randomUUID(), _id = new ObjectId())
    JavaBaseTypesDAO.insertOne(javaBaseTypes).result()
    JavaBaseTypesDAO.insertOne(second).result()
    JavaBaseTypesDAO.insertOne(javaBaseTypes.copy(uuid = UUID.randomUUID(), _id = new ObjectId())).result()
    val result = JavaBaseTypesDAO.find(Filter.valueFilter("uuid", List(javaBaseTypes.uuid, second.uuid))).resultList()
    assertEquals(result.map(_._id).toSet, Set(javaBaseTypes._id, second._id))
  }

  test("read java.util.UUID stored as legacy BSON binary") {
    val uuid = UUID.randomUUID()
    val id   = new ObjectId()
    UuidDocumentDAO.insertOne(Document("_id" -> id, "uuid" -> new BsonBinary(uuid, UuidRepresentation.JAVA_LEGACY))).result()
    assertEquals(UuidDAO.find("_id", id).result(), UuidTest(uuid, id))
  }

  test("read java.util.UUID stored as BSON binary") {
    val uuid = UUID.randomUUID()
    val id   = new ObjectId()
    UuidDocumentDAO.insertOne(Document("_id" -> id, "uuid" -> new BsonBinary(uuid))).result()
    assertEquals(UuidDAO.find("_id", id).result(), UuidTest(uuid, id))
  }

}
