package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.Document

import java.util.TimeZone

class LuceneSearchSuite extends BasePersonSuite {
  lazy val sortByBalance: Map[String, Int] = Map("balance" -> -1)
  TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

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
    val luceneQuery = LuceneQueryConverter.parse("registered:20140419T224427000\\+0200", "unbekannt")
    val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
    assertEquals(search.size, 1)
    assertEquals(search.head.age, 31)
    assertEquals(search.head.name, "Latasha Mcmillan")
    assertEquals(search.head.balance, 3403.0)
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
    val document      = LuceneQueryConverter.toDocument(luceneQuery)
    assertEquals("{\"$and\": [{\"$nor\": [{\"fieldName\": {\"$eq\": \"value1\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}]}]}", document.asInstanceOf[Document].toJson())
    val luceneQuery2 = LuceneQueryConverter.parse("NOT fieldName:('value1' AND 'value2' AND 'value2')", "ube")
    val document2      = LuceneQueryConverter.toDocument(luceneQuery2)
    assertEquals("{\"$and\": [{\"$nor\": [{\"fieldName\": {\"$eq\": \"value1\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}, {\"fieldName\": {\"$eq\": \"value2\"}}]}]}", document2.asInstanceOf[Document].toJson())
  }

  test("search for values with or") {
    val luceneQueryNegateWithAnd = LuceneQueryConverter.parse("-name:\"Latasha Mcmillan\" AND -name:\"Diaz Jacobs\"", "ube")
    val searchNegateWithAnd = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQueryNegateWithAnd), sortByBalance).resultList()
    assertEquals(searchNegateWithAnd.size, 198)

    val luceneQueryNegateWithOr = LuceneQueryConverter.parse("-name:(\"Latasha Mcmillan\" OR \"Diaz Jacobs\")", "ube")
    val searchNegateWithOr = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQueryNegateWithOr), sortByBalance).resultList()
    assertEquals(searchNegateWithOr.size, 198)

    val luceneQueryWithOr = LuceneQueryConverter.parse("name:(\"Latasha Mcmillan\" OR \"Diaz Jacobs\")", "ube")
    val searchWithOr = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQueryWithOr), sortByBalance).resultList()
    assertEquals(searchWithOr.size, 2)
  }

}
