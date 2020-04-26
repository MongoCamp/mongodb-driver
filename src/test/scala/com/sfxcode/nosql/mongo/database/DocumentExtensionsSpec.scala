package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification
import com.sfxcode.nosql.mongo.model.Person
import com.sfxcode.nosql.mongo.test.TestDatabase.PersonDAO
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.read
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

    "be converted to plain json " in {
      val document: Document = PersonDAO.Raw.find(Map("id" -> 11)).result()

      val s                = document.asPlainJson
      implicit val formats = DefaultFormats
      val person: Person   = read[Person](s)
      person.id mustEqual 11

      val tags = person.tags
      tags must haveSize(7)

      tags.head mustEqual "occaecat"

    }

  }

}
