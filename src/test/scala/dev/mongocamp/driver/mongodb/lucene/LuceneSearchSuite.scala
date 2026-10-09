package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import java.util.TimeZone
import org.mongodb.scala.Document

class LuceneSearchSuite extends BasePersonSuite {
  lazy val sortByBalance: Map[String, Int] = Map("balance" -> -1)

  test("search with with number in string") {
    val luceneQuery = LuceneQueryConverter.parse("stringNumber: 123", "id")
    val search2     = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search2.size, 0)
    val search = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery, searchWithValueAndString = true), sortByBalance).resultList()
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 25)
    assertEquals(search.head.name, "Cheryl Hoffman")
  }

  test("search with extended query") {
    val luceneQuery = LuceneQueryConverter.parse("(favoriteFruit:\"apple\" AND age:\"25\") OR name:*Cecile* AND -active:false AND 123", "id")
    // #region lucene-parser-with-explicit
    val search = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    // #endregion lucene-parser-with-explicit
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 25)
    assertEquals(search.head.name, "Terra Salinas")
  }

  test("search with extended query use implicit") {
    // #region lucene-parser
    val luceneQuery = LuceneQueryConverter.parse("(favoriteFruit:\"apple\" AND age:\"25\") OR name:*Cecile* AND -active:false AND 123", "id")
    // #endregion lucene-parser
    // #region lucene-parser-with-implicit
    val search = PersonDAO.find(luceneQuery, sortByBalance).resultList()
    // #endregion lucene-parser-with-implicit
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 25)
    assertEquals(search.head.name, "Terra Salinas")
  }

  test("between filter for number value") {
    val luceneQuery = LuceneQueryConverter.parse("[1010 TO 1052.3]", "balance")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 3)
    assertEquals(search.head.age, 28)
    assertEquals(search.head.name, "Mason Donaldson")
    assertEquals(search.last.name, "Nash Dunn")
  }

  test("between filter for number value not") {
    val luceneQuery = LuceneQueryConverter.parse("-[1010 TO 1052.3]", "balance")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 197)
    assertEquals(search.head.age, 29)
    assertEquals(search.head.balance, 3996.0)
    assertEquals(search.head.name, "Diaz Jacobs")
  }

  test("between filter for date value") {
    val luceneQuery    = LuceneQueryConverter.parse("[2014-04-20T00:00:00Z TO 2014-04-22T23:59:59Z]", "registered")
    val luceneDocument = LuceneQueryConverter.toDocument(luceneQuery)
    val expected       = "Iterable((registered,{\"$lte\": {\"$date\": \"2014-04-22T23:59:59Z\"}, \"$gte\": {\"$date\": \"2014-04-20T00:00:00Z\"}}))"
    assertEquals(luceneDocument.toString, expected)
    val search = PersonDAO.find(luceneDocument, sortByBalance).resultList()
    assertEquals(search.size, 7)
    assertEquals(search.head.age, 25)
    assertEquals(search.head.name, "Allison Turner")
    assertEquals(search.head.balance, 3961.0)
  }

  test("equals Query with Date") {
    List(
      "registered:20140420T004427000\\+0200",
      "registered:20140419T224427000Z",
      "registered:2014-04-19T22\\:44\\:27Z",
      "registered:\"2014-04-20T00:44:27+02:00\""
    ).foreach(
      query => {
        val luceneQuery = LuceneQueryConverter.parse(query, "unbekannt")
        val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
        assertEquals(search.map(_.name), List("Latasha Mcmillan"), query)
      }
    )
  }

  test("equals Query with Date without time zone offset uses the default time zone of the JVM") {
    val defaultTimeZone = TimeZone.getDefault
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("Europe/Berlin"))
      val luceneQuery = LuceneQueryConverter.parse("registered:20140420T004427000", "unbekannt")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      assertEquals(search.map(_.name), List("Latasha Mcmillan"))
    }
    finally TimeZone.setDefault(defaultTimeZone)
  }

  test("search with custom tokenizer") {
    // #region lucene-parser-with-tokenizer
    val analyzer    = new MongoCampLuceneAnalyzer(tokenizerFactory = () => new MongoCampTokenizer(maxTokenLength = 255))
    val queryParser = new MongoCampLuceneQueryParser("name", analyzer)
    val luceneQuery = queryParser.parse("email:latashamcmillan@ultrimax.com")
    analyzer.close()
    val search = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    // #endregion lucene-parser-with-tokenizer
    assertEquals(search.map(_.name), List("Latasha Mcmillan"))
  }

  test("quoted value is searched as exact value") {
    // #region lucene-exact-value
    val luceneQuery = LuceneQueryConverter.parse("name:\"Latasha Mcmillan\"", "unbekannt")
    // #endregion lucene-exact-value
    assertEquals(PersonDAO.find(luceneQuery, sortByBalance).resultList().map(_.name), List("Latasha Mcmillan"))
    List("name:\"Latasha\"", "name:\"Mcmillan Latasha\"", "name:\"latasha mcmillan\"", "name:\"Latasha  Mcmillan\"").foreach(
      query => assertEquals(PersonDAO.find(LuceneQueryConverter.parse(query, "unbekannt"), sortByBalance).resultList().size, 0, query)
    )
    assertEquals(PersonDAO.find(LuceneQueryConverter.parse("-name:\"Latasha Mcmillan\"", "unbekannt"), sortByBalance).resultList().size, 199)
  }

  test("wildcard Query has to match the whole value") {
    val queries = Map(
      "name:\"*sha Mcmil*\""   -> List("Latasha Mcmillan"),
      "name:\"*SHA MCMIL*\""   -> List("Latasha Mcmillan"),
      "name:\"Latasha Mc*\""   -> List("Latasha Mcmillan"),
      "name:\"*sha Mcmillan\"" -> List("Latasha Mcmillan"),
      "name:\"sha Mcmil*\""    -> List(),
      "name:\"*sha Mcmil\""    -> List(),
      "name:Latash*"           -> List("Latasha Mcmillan"),
      "name:atasha*"           -> List()
    )
    queries.foreach {
      case (query, expected) =>
        assertEquals(PersonDAO.find(LuceneQueryConverter.parse(query, "unbekannt"), sortByBalance).resultList().map(_.name), expected, query)
    }
  }

  test("wildcard Query matches values with line breaks") {
    val dao = new MongoDAO[Document](provider, "lucene-line-breaks") {}
    dao.drop().result()
    dao.insertOne(Document("text" -> "first line\nsecond line")).result()
    def count(query: String): Int = dao.find(LuceneQueryConverter.parse(query, "text")).resultList().size
    List("text:*second*", "text:first*", "text:*line", "text:*line?second*", "text:\"first line*second line\"").foreach(
      query => assertEquals(count(query), 1, query)
    )
    List("text:*third*", "text:\"*line second*\"").foreach(
      query => assertEquals(count(query), 0, query)
    )
    dao.drop().result()
  }

  test("equals Query with email address") {
    List("email:latashamcmillan@ultrimax.com", "email:\"latashamcmillan@ultrimax.com\"", "latashamcmillan@ultrimax.com").foreach(
      query => {
        val search = PersonDAO.find(LuceneQueryConverter.parse(query, "email"), sortByBalance).resultList()
        assertEquals(search.map(_.name), List("Latasha Mcmillan"), query)
      }
    )
  }

  test("not equals Query with email address") {
    val search = PersonDAO.find(LuceneQueryConverter.parse("-email:latashamcmillan@ultrimax.com", "ube"), sortByBalance).resultList()
    assertEquals(search.size, 199)
  }

  test("wildcard Query with email address") {
    List("email:*@ultrimax.com", "email:latashamcmillan@*", "email:*mcmillan@ultri*", "email:latashamcmillan@ultrimax?com").foreach(
      query => {
        val search = PersonDAO.find(LuceneQueryConverter.parse(query, "ube"), sortByBalance).resultList()
        assertEquals(search.map(_.name), List("Latasha Mcmillan"), query)
      }
    )
  }

  test("wildcard Query with email address escapes regex characters") {
    val search = PersonDAO.find(LuceneQueryConverter.parse("email:*@ultrimax.co.", "ube"), sortByBalance).resultList()
    assertEquals(search.size, 0)
  }

  test("wildcard at the end") {
    val luceneQuery = LuceneQueryConverter.parse("Latasha*", "name")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 31)
    assertEquals(search.head.name, "Latasha Mcmillan")
    assertEquals(search.head.balance, 3403.0)
  }

  test("wildcard at the start") {
    val luceneQuery = LuceneQueryConverter.parse("*Mcmillan", "name")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 31)
    assertEquals(search.head.name, "Latasha Mcmillan")
    assertEquals(search.head.balance, 3403.0)
  }

  test("not wildcard at the start") {
    val luceneQuery = LuceneQueryConverter.parse("-name:*Mcmillan", "ube")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 199)
  }

  test("wildcard at the start and end") {
    val luceneQuery = LuceneQueryConverter.parse("*Mcmil*", "name")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 31)
    assertEquals(search.head.name, "Latasha Mcmillan")
    assertEquals(search.head.balance, 3403.0)
  }

  test("not wildcard at the start and end") {
    val luceneQuery = LuceneQueryConverter.parse("-name:*Mcmil*", "ube")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 199)
  }

  test("wildcard in the middle") {
    val luceneQuery = LuceneQueryConverter.parse("\"Latasha *millan\"", "name")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 31)
    assertEquals(search.head.name, "Latasha Mcmillan")
    assertEquals(search.head.balance, 3403.0)
  }

  test("not wildcard in the middle") {
    val luceneQuery = LuceneQueryConverter.parse("-name:\"Latasha*millan\"", "ube")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 199)
  }

  test("negate query with values in braces") {
    val luceneQuery = LuceneQueryConverter.parse("NOT fieldName:('value1' OR 'value2' OR 'value2')", "ube")
    val document    = LuceneQueryConverter.toDocument(luceneQuery)
    assertEquals(
      "{\"$and\": [{\"$nor\": [{\"fieldName\": {\"$eq\": \"value1\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}]}]}",
      document.asInstanceOf[Document].toJson()
    )
    val luceneQuery2 = LuceneQueryConverter.parse("NOT fieldName:('value1' AND 'value2' AND 'value2')", "ube")
    val document2    = LuceneQueryConverter.toDocument(luceneQuery2)
    assertEquals(
      "{\"$and\": [{\"$nor\": [{\"fieldName\": {\"$eq\": \"value1\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}]}]}",
      document2.asInstanceOf[Document].toJson()
    )
  }

  test("search for values with or") {
    val luceneQueryNegateWithAnd = LuceneQueryConverter.parse("-name:\"Latasha Mcmillan\" AND -name:\"Diaz Jacobs\"", "ube")
    val searchNegateWithAnd      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQueryNegateWithAnd), sortByBalance).resultList()
    assertEquals(searchNegateWithAnd.size, 198)

    val luceneQueryNegateWithOr = LuceneQueryConverter.parse("-name:(\"Latasha Mcmillan\" OR \"Diaz Jacobs\")", "ube")
    val searchNegateWithOr      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQueryNegateWithOr), sortByBalance).resultList()
    assertEquals(searchNegateWithOr.size, 198)

    val luceneQueryWithOr = LuceneQueryConverter.parse("name:(\"Latasha Mcmillan\" OR \"Diaz Jacobs\")", "ube")
    val searchWithOr      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQueryWithOr), sortByBalance).resultList()
    assertEquals(searchWithOr.size, 2)
  }

}
