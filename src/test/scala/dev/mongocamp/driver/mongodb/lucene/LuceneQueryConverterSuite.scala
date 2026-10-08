package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import java.util.TimeZone
import org.bson.BsonDocument
import org.mongodb.scala.bson.conversions.Bson

class LuceneQueryConverterSuite extends munit.FunSuite {

  private def toJson(query: String, defaultField: String = "default"): String = {
    val bson: Bson = LuceneQueryConverter.toDocument(LuceneQueryConverter.parse(query, defaultField))
    bson.toBsonDocument.toJson
  }

  private def assertQuery(query: String, expectedJson: String, defaultField: String = "default"): Unit = {
    assertEquals(BsonDocument.parse(toJson(query, defaultField)), BsonDocument.parse(expectedJson), query)
  }

  test("email address is one term") {
    assertQuery("email:john.doe@example.com", """{"email": {"$eq": "john.doe@example.com"}}""")
    assertQuery("email:\"john.doe@example.com\"", """{"email": {"$eq": "john.doe@example.com"}}""")
    assertQuery("john.doe@example.com", """{"email": {"$eq": "john.doe@example.com"}}""", "email")
    assertQuery("email:john+tag@example.com", """{"email": {"$eq": "john+tag@example.com"}}""")
    assertQuery("email:john\\+tag@example.com", """{"email": {"$eq": "john+tag@example.com"}}""")
    assertQuery("-email:john.doe@example.com", """{"$and": [{"email": {"$ne": "john.doe@example.com"}}]}""")
    assertQuery("email:'john.doe@example.com'", """{"email": {"$eq": "john.doe@example.com"}}""")
    assertQuery("email:o'brien@example.com", """{"email": {"$eq": "o'brien@example.com"}}""")
  }

  test("wildcard query escapes regex characters") {
    assertQuery("email:*@example.com", """{"email": {"$regex": "(.*?)@example\\.com", "$options": "i"}}""")
    assertQuery("email:*john+tag@example.com", """{"email": {"$regex": "(.*?)john\\+tag@example\\.com", "$options": "i"}}""")
    assertQuery("email:john?doe@*.com", """{"email": {"$regex": "john.doe@(.*?)\\.com", "$options": "i"}}""")
    assertQuery("-email:*@example.com", """{"$and": [{"email": {"$not": {"$regex": "(.*?)@example\\.com", "$options": "i"}}}]}""")
  }

  test("prefix query escapes regex characters") {
    assertQuery("email:john.doe@*", """{"$and": [{"email": {"$regex": "john\\.doe@(.*?)", "$options": "i"}}]}""")
    assertQuery("email:john+tag*", """{"$and": [{"email": {"$regex": "john\\+tag(.*?)", "$options": "i"}}]}""")
    assertQuery("version:1.2*", """{"$and": [{"version": {"$regex": "1\\.2(.*?)", "$options": "i"}}]}""")
  }

  test("phrase query escapes regex characters and supports wildcards") {
    assertQuery(
      "name:\"Latasha *millan\"",
      """{"$and": [{"name": {"$regex": "(.*?)Latasha(.*?)", "$options": "i"}}, {"name": {"$regex": "(.*?)(.*?)millan(.*?)", "$options": "i"}}]}"""
    )
    assertQuery(
      "mail:\"a.b c+d\"",
      """{"$and": [{"mail": {"$regex": "(.*?)a\\.b(.*?)", "$options": "i"}}, {"mail": {"$regex": "(.*?)c\\+d(.*?)", "$options": "i"}}]}"""
    )
  }

  test("quoted value with wildcard is a wildcard query") {
    assertQuery("name:\"Latasha*millan\"", """{"name": {"$regex": "Latasha(.*?)millan", "$options": "i"}}""")
    assertQuery("-name:\"Latasha*millan\"", """{"$and": [{"name": {"$not": {"$regex": "Latasha(.*?)millan", "$options": "i"}}}]}""")
  }

  test("date with time zone offset keeps the offset") {
    val expected = """{"registered": {"$eq": {"$date": "2014-04-19T20:44:27Z"}}}"""
    assertQuery("registered:20140419T224427000\\+0200", expected)
    assertQuery("registered:20140419T224427\\+0200", expected)
    assertQuery("registered:\"2014-04-19T22:44:27+02:00\"", expected)
    assertQuery("registered:2014-04-19T22\\:44\\:27.000+02\\:00", expected)
  }

  test("date without time zone offset is UTC") {
    val expected = """{"registered": {"$eq": {"$date": "2014-04-19T22:44:27Z"}}}"""
    assertQuery("registered:20140419T224427000", expected)
    assertQuery("registered:20140419T224427000Z", expected)
    assertQuery("registered:20140419T224427", expected)
    assertQuery("registered:20140419T224427Z", expected)
    assertQuery("registered:2014-04-19T22\\:44\\:27", expected)
    assertQuery("registered:2014-04-19T22\\:44\\:27Z", expected)
    assertQuery("registered:20140419T2244", """{"registered": {"$eq": {"$date": "2014-04-19T22:44:00Z"}}}""")
    assertQuery("registered:20140419T2244Z", """{"registered": {"$eq": {"$date": "2014-04-19T22:44:00Z"}}}""")
    assertQuery("registered:2014-04-19", """{"registered": {"$eq": {"$date": "2014-04-19T00:00:00Z"}}}""")
  }

  test("date range respects time zone offset") {
    assertQuery(
      "registered:[2014-04-20T00\\:00\\:00+02\\:00 TO 2014-04-22T23\\:59\\:59+02\\:00]",
      """{"registered": {"$lte": {"$date": "2014-04-22T21:59:59Z"}, "$gte": {"$date": "2014-04-19T22:00:00Z"}}}"""
    )
  }

  test("date parsing does not depend on the default time zone") {
    val defaultTimeZone     = TimeZone.getDefault
    val defaultJodaTimeZone = org.joda.time.DateTimeZone.getDefault
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("Pacific/Kiritimati"))
      org.joda.time.DateTimeZone.setDefault(org.joda.time.DateTimeZone.forID("America/New_York"))
      assertQuery("registered:20140419T224427000", """{"registered": {"$eq": {"$date": "2014-04-19T22:44:27Z"}}}""")
      assertQuery("registered:2014-04-19T22\\:44\\:27", """{"registered": {"$eq": {"$date": "2014-04-19T22:44:27Z"}}}""")
      assertQuery("registered:20140419T224427000\\+0200", """{"registered": {"$eq": {"$date": "2014-04-19T20:44:27Z"}}}""")
    }
    finally {
      TimeZone.setDefault(defaultTimeZone)
      org.joda.time.DateTimeZone.setDefault(defaultJodaTimeZone)
    }
  }

  test("date parsing is strict") {
    assertQuery("code:20140419T224427000X", """{"code": {"$eq": "20140419T224427000X"}}""")
    assertQuery("code:20140419T22442", """{"code": {"$eq": "20140419T22442"}}""")
    assertQuery("code:20141319T224427", """{"code": {"$eq": "20141319T224427"}}""")
  }

}
