package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase._

class LuceneSearchSpec extends PersonSpecification {
  lazy val sortByBalance = Map("balance" -> -1)

  "LuceneSearch" should {

    "search with extended query" in {
      val luceneQuery = LuceneQueryConverter.parse("(favoriteFruit:\"apple\" AND age:\"25\") OR name:*Cecile* AND -active:false AND 123", "id")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 25
      search.head.name mustEqual "Terra Salinas"
    }

    "between filter for number value" in {
      val luceneQuery = LuceneQueryConverter.parse("[1010 TO 1052.3]", "balance")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(3)
      search.head.age mustEqual 28
      search.head.name mustEqual "Mason Donaldson"
      search.last.name mustEqual "Nash Dunn"
    }

    "between filter for number value not" in {
      val luceneQuery = LuceneQueryConverter.parse("-[1010 TO 1052.3]", "balance")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(197)
      search.head.age mustEqual 29
      search.head.balance mustEqual 3996.0
      search.head.name mustEqual "Diaz Jacobs"
    }

    "between filter for date value" in {
      val luceneQuery = LuceneQueryConverter.parse("[2014-04-20T00:00:00Z TO 2014-04-22T23:59:59Z]", "registered")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(10)
      search.head.age mustEqual 25
      search.head.name mustEqual "Allison Turner"
      search.head.balance mustEqual 3961.0
    }

    "equals Query with Date" in {
      val luceneQuery = LuceneQueryConverter.parse("registered:20140420T004427000+0200", "unbekannt")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "wildcard at the end" in {
      val luceneQuery = LuceneQueryConverter.parse("Latasha*", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "wildcard at the start" in {
      val luceneQuery = LuceneQueryConverter.parse("*Mcmillan", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "not wildcard at the start" in {
      val luceneQuery = LuceneQueryConverter.parse("-name:*Mcmillan", "ube")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(199)
    }

    "wildcard at the start and end" in {
      val luceneQuery = LuceneQueryConverter.parse("*Mcmil*", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "not wildcard at the start and end" in {
      val luceneQuery = LuceneQueryConverter.parse("-name:*Mcmil*", "ube")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(199)
    }

    "wildcard in the middle" in {
      val luceneQuery = LuceneQueryConverter.parse("\"Latasha *millan\"", "name")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(1)
      search.head.age mustEqual 31
      search.head.name mustEqual "Latasha Mcmillan"
      search.head.balance mustEqual 3403.0
    }

    "not wildcard in the middle" in {
      val luceneQuery = LuceneQueryConverter.parse("-name:\"Latasha*millan\"", "ube")
      val search      = PersonDAO.find(LuceneQueryConverter.getMongoDbSearch(luceneQuery), sortByBalance).resultList()
      search must haveSize(199)
    }
  }

}
