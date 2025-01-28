package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDAO
import org.mongodb.scala.Document

class DocumentExtensionsSpec extends PersonSpecification {

  "Document" should {

    "be converted to plain scala map" in {
      val document: Document = PersonDAO.Raw.find(Map("id" -> 11)).result()

      val map: Map[String, Any] = document.asPlainMap
      map("id") mustEqual 11

      val tags = map("tags").asInstanceOf[List[String]]
      tags must haveSize(7)

      tags.head mustEqual "occaecat"
    }

  }

}
