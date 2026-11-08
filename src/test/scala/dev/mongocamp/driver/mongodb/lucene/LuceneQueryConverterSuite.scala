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
    assertQuery("email:*@example.com", """{"email": {"$regex": "^(.*?)@example\\.com$", "$options": "is"}}""")
    assertQuery("email:*john+tag@example.com", """{"email": {"$regex": "^(.*?)john\\+tag@example\\.com$", "$options": "is"}}""")
    assertQuery("email:john?doe@*.com", """{"email": {"$regex": "^john.doe@(.*?)\\.com$", "$options": "is"}}""")
    assertQuery("-email:*@example.com", """{"$and": [{"email": {"$not": {"$regex": "^(.*?)@example\\.com$", "$options": "is"}}}]}""")
  }

  test("wildcard query has to match the whole value") {
    assertQuery("name:John*", """{"$and": [{"name": {"$regex": "^John(.*?)$", "$options": "is"}}]}""")
    assertQuery("name:*Dowe", """{"name": {"$regex": "^(.*?)Dowe$", "$options": "is"}}""")
    assertQuery("name:*John*", """{"name": {"$regex": "^(.*?)John(.*?)$", "$options": "is"}}""")
    assertQuery("name:\"John Dowe*\"", """{"name": {"$regex": "^John Dowe(.*?)$", "$options": "is"}}""")
    assertQuery("name:\"*John Dowe\"", """{"name": {"$regex": "^(.*?)John Dowe$", "$options": "is"}}""")
    assertQuery("name:\"*John Dowe*\"", """{"name": {"$regex": "^(.*?)John Dowe(.*?)$", "$options": "is"}}""")
    assertQuery("name:J?hn", """{"name": {"$regex": "^J.hn$", "$options": "is"}}""")
  }

  test("prefix query escapes regex characters") {
    assertQuery("email:john.doe@*", """{"$and": [{"email": {"$regex": "^john\\.doe@(.*?)$", "$options": "is"}}]}""")
    assertQuery("email:john+tag*", """{"$and": [{"email": {"$regex": "^john\\+tag(.*?)$", "$options": "is"}}]}""")
    assertQuery("version:1.2*", """{"$and": [{"version": {"$regex": "^1\\.2(.*?)$", "$options": "is"}}]}""")
  }

  test("quoted value is searched as exact value") {
    assertQuery("name:\"Hallo Welt\"", """{"name": {"$eq": "Hallo Welt"}}""")
    assertQuery("\"Hallo Welt\"", """{"name": {"$eq": "Hallo Welt"}}""", "name")
    assertQuery("name:\"Hallo  Welt \"", """{"name": {"$eq": "Hallo  Welt "}}""")
    assertQuery("name:\"Hallo \\\"Welt\\\"\"", """{"name": {"$eq": "Hallo \"Welt\""}}""")
    assertQuery("name:\"a.b c+d (e)\"", """{"name": {"$eq": "a.b c+d (e)"}}""")
    assertQuery("name:\"'Hallo Welt'\"", """{"name": {"$eq": "'Hallo Welt'"}}""")
    assertQuery("name:\"Hallo Welt\"~2", """{"name": {"$eq": "Hallo Welt"}}""")
    assertQuery("-name:\"Hallo Welt\"", """{"$and": [{"name": {"$ne": "Hallo Welt"}}]}""")
    assertQuery("name:(\"Hallo Welt\" OR \"Hello World\")", """{"$or": [{"name": {"$eq": "Hallo Welt"}}, {"name": {"$eq": "Hello World"}}]}""")
  }

  test("phrase query of other parsers is searched as exact value") {
    val phraseQuery = new org.apache.lucene.queryparser.classic.QueryParser("name", new MongoCampLuceneAnalyzer()).parse("name:\"Hallo  Welt\"")
    assert(phraseQuery.isInstanceOf[org.apache.lucene.search.PhraseQuery], phraseQuery.getClass.getName)
    val bson: Bson = LuceneQueryConverter.toDocument(phraseQuery)
    assertEquals(bson.toBsonDocument, BsonDocument.parse("""{"name": {"$eq": "Hallo Welt"}}"""))
  }

  test("quoted value with wildcard is a wildcard query") {
    assertQuery("name:\"Wie geht's?\"", """{"name": {"$eq": "Wie geht's?"}}""")
    assertQuery("name:\"Latasha *millan\"", """{"name": {"$regex": "^Latasha (.*?)millan$", "$options": "is"}}""")
    assertQuery("name:\"a.b *\"", """{"name": {"$regex": "^a\\.b (.*?)$", "$options": "is"}}""")
    assertQuery("name:\"Latasha*millan\"", """{"name": {"$regex": "^Latasha(.*?)millan$", "$options": "is"}}""")
    assertQuery("-name:\"Latasha*millan\"", """{"$and": [{"name": {"$not": {"$regex": "^Latasha(.*?)millan$", "$options": "is"}}}]}""")
  }

  test("date with time zone offset keeps the offset") {
    val expected = """{"registered": {"$eq": {"$date": "2014-04-19T20:44:27Z"}}}"""
    assertQuery("registered:20140419T224427000\\+0200", expected)
    assertQuery("registered:20140419T224427\\+0200", expected)
    assertQuery("registered:\"2014-04-19T22:44:27+02:00\"", expected)
    assertQuery("registered:2014-04-19T22\\:44\\:27.000+02\\:00", expected)
  }

  private def withDefaultTimeZone[T](timeZoneId: String)(body: => T): T = {
    val defaultTimeZone = TimeZone.getDefault
    try {
      TimeZone.setDefault(TimeZone.getTimeZone(timeZoneId))
      body
    }
    finally TimeZone.setDefault(defaultTimeZone)
  }

  test("date with Z is UTC") {
    withDefaultTimeZone("Europe/Berlin") {
      val expected = """{"registered": {"$eq": {"$date": "2014-04-19T22:44:27Z"}}}"""
      assertQuery("registered:20140419T224427000Z", expected)
      assertQuery("registered:20140419T224427Z", expected)
      assertQuery("registered:2014-04-19T22\\:44\\:27Z", expected)
      assertQuery("registered:20140419T2244Z", """{"registered": {"$eq": {"$date": "2014-04-19T22:44:00Z"}}}""")
    }
  }

  test("date without time zone offset uses the default time zone of the JVM") {
    withDefaultTimeZone("UTC") {
      val expected = """{"registered": {"$eq": {"$date": "2014-04-19T22:44:27Z"}}}"""
      assertQuery("registered:20140419T224427000", expected)
      assertQuery("registered:20140419T224427", expected)
      assertQuery("registered:2014-04-19T22\\:44\\:27", expected)
      assertQuery("registered:20140419T2244", """{"registered": {"$eq": {"$date": "2014-04-19T22:44:00Z"}}}""")
      assertQuery("registered:2014-04-19", """{"registered": {"$eq": {"$date": "2014-04-19T00:00:00Z"}}}""")
    }
    withDefaultTimeZone("Europe/Berlin") {
      val expected = """{"registered": {"$eq": {"$date": "2014-04-19T20:44:27Z"}}}"""
      assertQuery("registered:20140419T224427000", expected)
      assertQuery("registered:20140419T224427", expected)
      assertQuery("registered:2014-04-19T22\\:44\\:27", expected)
      assertQuery("registered:2014-04-19", """{"registered": {"$eq": {"$date": "2014-04-18T22:00:00Z"}}}""")
      // a time zone offset is stronger than the default time zone
      assertQuery("registered:20140419T224427000\\+0200", expected)
      assertQuery("registered:\"2014-04-19T22:44:27+02:00\"", expected)
    }
  }

  test("date range respects time zone offset") {
    assertQuery(
      "registered:[2014-04-20T00\\:00\\:00+02\\:00 TO 2014-04-22T23\\:59\\:59+02\\:00]",
      """{"registered": {"$lte": {"$date": "2014-04-22T21:59:59Z"}, "$gte": {"$date": "2014-04-19T22:00:00Z"}}}"""
    )
  }

  test("date parsing is strict") {
    assertQuery("code:20140419T224427000X", """{"code": {"$eq": "20140419T224427000X"}}""")
    assertQuery("code:20140419T22442", """{"code": {"$eq": "20140419T22442"}}""")
    assertQuery("code:20141319T224427", """{"code": {"$eq": "20141319T224427"}}""")
  }

  test("open ranges") {
    assertQuery("n:[5 TO *]", """{"n": {"$gte": 5}}""")
    assertQuery("n:[* TO 5]", """{"n": {"$lte": 5}}""")
    assertQuery("n:{5 TO *]", """{"n": {"$gt": 5}}""")
    assertQuery("n:[* TO 5}", """{"n": {"$lt": 5}}""")
    assertQuery("n:[5 TO 50]", """{"n": {"$gte": 5, "$lte": 50}}""")
    assertQuery("n:{5 TO 50}", """{"n": {"$gt": 5, "$lt": 50}}""")
    assertQuery("-n:[5 TO *]", """{"$and": [{"n": {"$not": {"$gte": 5}}}]}""")
    assertQuery("d:[2018-01-01T00:00:00Z TO *]", """{"d": {"$gte": {"$date": "2018-01-01T00:00:00Z"}}}""")
    assertQuery("d:[* TO 2018-01-01T00:00:00Z]", """{"d": {"$lte": {"$date": "2018-01-01T00:00:00Z"}}}""")
    assertQuery("n:[* TO *]", """{"n": {"$exists": true}}""")
  }

  test("comparison operators") {
    assertQuery("n:>=5", """{"n": {"$gte": 5}}""")
    assertQuery("n:>5", """{"n": {"$gt": 5}}""")
    assertQuery("n:<=5", """{"n": {"$lte": 5}}""")
    assertQuery("n:<5", """{"n": {"$lt": 5}}""")
    assertQuery("n:>=2.5", """{"n": {"$gte": 2.5}}""")
    assertQuery("-n:>=5", """{"$and": [{"n": {"$not": {"$gte": 5}}}]}""")
    assertQuery("n:>=5 AND n:<10", """{"$and": [{"n": {"$gte": 5}}, {"n": {"$lt": 10}}]}""")
    assertQuery("d:>=2018-01-01T00\\:00\\:00Z", """{"d": {"$gte": {"$date": "2018-01-01T00:00:00Z"}}}""")
    // quoted values are exact values
    assertQuery("n:\">=5\"", """{"n": {"$eq": ">=5"}}""")
  }

  test("exists") {
    assertQuery("_exists_:opt", """{"opt": {"$exists": true}}""")
    assertQuery("-_exists_:opt", """{"$and": [{"opt": {"$exists": false}}]}""")
    assertQuery("NOT _exists_:opt", """{"$and": [{"opt": {"$exists": false}}]}""")
    assertQuery("_exists_:opt AND a:b", """{"$and": [{"opt": {"$exists": true}}, {"a": {"$eq": "b"}}]}""")
    assertQuery("n:*", """{"n": {"$exists": true}}""")
    assertQuery("_id:*", """{"_id": {"$exists": true}}""")
    assertQuery("name:*", """{"name": {"$exists": true}}""")
    assertQuery("-n:*", """{"$and": [{"n": {"$exists": false}}]}""")
  }

  test("match all documents") {
    assertQuery("*:*", """{}""")
  }

  test("unsupported queries throw IllegalArgumentException") {
    List("name:foo~", "name:foo^2").foreach(
      query => intercept[IllegalArgumentException](toJson(query))
    )
  }

}
