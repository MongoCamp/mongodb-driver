package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.joda.time.DateTime

import java.util.TimeZone

class LuceneSearchSpec extends PersonSpecification {
  lazy val sortByBalance: Map[String, Int] = Map("balance" -> -1)
  TimeZone.setDefault(TimeZone.getTimeZone("UTC"))

  "LuceneSearch" should {

    "search with with number in string" in {
      val luceneQuery = LuceneQueryConverter.parse("stringNumber: 123", "id")
      val search2     = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search2 must haveSize(0)
      val search = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery, searchWithValueAndString = true), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 25
      search.head.name mustEqual "Cheryl Hoffman"
    }

    "search with extended query" in {
      val luceneQuery = LuceneQueryConverter.parse("(favoriteFruit:\"apple\" AND age:\"25\") OR name:*Cecile* AND -active:false AND 123", "id")
      // #region lucene-parser-with-explicit
      val search = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      // #endregion lucene-parser-with-explicit
      search must haveSize(1)
      search.head.age mustEqual 25
      search.head.name mustEqual "Terra Salinas"
    }

    "search with extended query use implicit" in {
      // #region lucene-parser
      val luceneQuery = LuceneQueryConverter.parse("(favoriteFruit:\"apple\" AND age:\"25\") OR name:*Cecile* AND -active:false AND 123", "id")
      // #endregion lucene-parser
      // #region lucene-parser-with-implicit
      val search = PersonDAO.find(luceneQuery, sortByBalance).resultList()
      // #endregion lucene-parser-with-implicit
      search must haveSize(1)
      search.head.age mustEqual 25
      search.head.name mustEqual "Terra Salinas"
    }

    "between filter for number value" in {
      val luceneQuery = LuceneQueryConverter.parse("[1010 TO 1052.3]", "balance")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(3)
      search.head.age mustEqual 28
      search.head.name mustEqual "Mason Donaldson"
      search.last.name mustEqual "Nash Dunn"
    }

    "between filter for number value not" in {
      val luceneQuery = LuceneQueryConverter.parse("-[1010 TO 1052.3]", "balance")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(197)
      search.head.age mustEqual 29
      search.head.balance mustEqual 3996.0
      search.head.name mustEqual "Diaz Jacobs"
    }

    "between filter for date value" in {
      val luceneQuery    = LuceneQueryConverter.parse("[2014-04-20T00:00:00Z TO 2014-04-22T23:59:59Z]", "registered")
      val luceneDocument = LuceneQueryConverter.toDocument(luceneQuery)
      val expected       = "Iterable((registered,{\"$lte\": {\"$date\": \"2014-04-22T23:59:59Z\"}, \"$gte\": {\"$date\": \"2014-04-20T00:00:00Z\"}}))"
      luceneDocument.toString must beEqualTo(expected)
      val search = PersonDAO.find(luceneDocument, sortByBalance).resultList()
      search must haveSize(7)
      search.head.age mustEqual 25
      search.head.name mustEqual "Allison Turner"
      search.head.balance mustEqual 3961.0
    }

    "equals Query with Date" in {
      val luceneQuery = LuceneQueryConverter.parse("registered:20140419T224427000\\+0200", "unbekannt")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "wildcard at the end" in {
      val luceneQuery = LuceneQueryConverter.parse("Latasha*", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "wildcard at the start" in {
      val luceneQuery = LuceneQueryConverter.parse("*Mcmillan", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "not wildcard at the start" in {
      val luceneQuery = LuceneQueryConverter.parse("-name:*Mcmillan", "ube")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(199)
    }

    "wildcard at the start and end" in {
      val luceneQuery = LuceneQueryConverter.parse("*Mcmil*", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "not wildcard at the start and end" in {
      val luceneQuery = LuceneQueryConverter.parse("-name:*Mcmil*", "ube")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(199)
    }

    "wildcard in the middle" in {
      val luceneQuery = LuceneQueryConverter.parse("\"Latasha *millan\"", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "not wildcard in the middle" in {
      val luceneQuery = LuceneQueryConverter.parse("-name:\"Latasha*millan\"", "ube")
      val search      = PersonDAO.find(LuceneQueryConverter.toDocument(luceneQuery), sortByBalance).resultList()
      search must haveSize(199)
    }
  }

}
