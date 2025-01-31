package dev.mongocamp.driver.mongodb.database

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDAO
import org.mongodb.scala.Document

class DocumentExtensionsSuite extends BasePersonSuite {

  test("Document should be converted to plain scala map") {
    val document: Document = PersonDAO.Raw.find(Map("id" -> 11)).result()

    val map: Map[String, Any] = document.asPlainMap
    assertEquals(map("id"), 11)

    val tags = map("tags").asInstanceOf[List[String]]
    assertEquals(tags.size, 7)

    assertEquals(tags.head, "occaecat")
  }

}
