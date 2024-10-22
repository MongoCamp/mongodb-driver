package dev.mongocamp.driver.mongodb.schema

import dev.mongocamp.driver.mongodb.test.TestDatabase.{PersonDAO, PersonDocumentDAO}
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.relation.RelationDemoDatabase._
import org.specs2.mutable.{Before, Specification}

import scala.util.Try

class SchemaSpec extends Specification with Before {

  sequential

  "Schema" should {
    "analyse Json Schema from document dao" in {
      // #region schema-analysis
      val schemaExplorer =  new SchemaExplorer()
      val schemaAnalysis = schemaExplorer.analyzeSchema(PersonDocumentDAO)
      // #endregion schema-analysis
      schemaJson.contains("\"$schema\":\"https://json-schema.org/draft/2020-12/schema\"") must beTrue
      schemaJson.contains("\"Friends\":") must beTrue
      schemaJson.contains("\"title\":\"Friends\"") must beTrue
      schemaJson.contains("\"People\":") must beTrue
      schemaJson.contains("\"title\":\"People\"") must beTrue
      val idPattern1 = schemaJson.contains("\"_id\":{\"pattern\":\"^([a-fA-F0-9]{2})+$\",\"type\":\"string\"}")
      val idPattern2 = schemaJson.contains("\"_id\":{\"type\":\"string\",\"pattern\":\"^([a-fA-F0-9]{2})+$\"}")
      (idPattern1 || idPattern2) must beTrue
      schemaJson.contains("\"isActive\":{\"type\":\"boolean\"}") must beTrue
    }

    "detect Json Schema from document dao" in {
      // #region schema-explorer
      val schemaExplorer =  new SchemaExplorer()
      val schema = schemaExplorer.detectSchema(PersonDocumentDAO)
      val schemaJson = schema.toJson
      // #endregion schema-explorer
      schemaJson.contains("\"$schema\":\"https://json-schema.org/draft/2020-12/schema\"") must beTrue
      schemaJson.contains("\"Friends\":") must beTrue
      schemaJson.contains("\"title\":\"Friends\"") must beTrue
      schemaJson.contains("\"People\":") must beTrue
      schemaJson.contains("\"title\":\"People\"") must beTrue
      val idPattern1 = schemaJson.contains("\"_id\":{\"pattern\":\"^([a-fA-F0-9]{2})+$\",\"type\":\"string\"}")
      val idPattern2 = schemaJson.contains("\"_id\":{\"type\":\"string\",\"pattern\":\"^([a-fA-F0-9]{2})+$\"}")
      (idPattern1 || idPattern2) must beTrue
      schemaJson.contains("\"isActive\":{\"type\":\"boolean\"}") must beTrue
    }

  }

  override def before: Any = {
    Try {
      UserDAO.drop().result()
      LoginDAO.drop().result()
      SimplePersonDAO.drop().result()
    }
    val personList = PersonDAO.find().resultList()
    personList.foreach { person =>
      UserDAO.insertOne(User(person.id, person.name, person.guid)).result()
      LoginDAO.insertOne(Login(person.guid, person.email, person.email.reverse)).result()
      person.friends.foreach { f =>
        SimplePersonDAO.insertOne(SimplePerson((person.id + 11) * (f.id + 3), f.name, person.id)).result()
      }
    }

  }
}
