package dev.mongocamp.driver.mongodb.schema

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.relation.RelationDemoDatabase._
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDAO
import dev.mongocamp.driver.mongodb.test.TestDatabase.PersonDocumentDAO
import scala.util.Try

class SchemaSuite extends BasePersonSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Try {
      UserDAO.drop().result()
      LoginDAO.drop().result()
      SimplePersonDAO.drop().result()
    }
    val personList = PersonDAO.find().resultList()
    personList.foreach {
      person =>
        UserDAO.insertOne(User(person.id, person.name, person.guid)).result()
        LoginDAO.insertOne(Login(person.guid, person.email, person.email.reverse)).result()
        person.friends.foreach {
          f =>
            SimplePersonDAO.insertOne(SimplePerson((person.id + 11) * (f.id + 3), f.name, person.id)).result()
        }
    }
  }

  test("analyse Json Schema from document dao") {
    // #region schema-analysis
    val schemaExplorer = new SchemaExplorer()
    val schemaAnalysis = schemaExplorer.analyzeSchema(PersonDocumentDAO)
    // #endregion schema-analysis
    assertEquals(schemaAnalysis.count, 200L)
    assertEquals(schemaAnalysis.sample, 200L)
    assertEquals(schemaAnalysis.percentageOfAnalysed, 1.0)
    assertEquals(schemaAnalysis.fields.size, 20)
    val idField = schemaAnalysis.fields.head
    assertEquals(idField.name, "_id")
    assertEquals(idField.fieldTypes.head.fieldType, "objectId")
    assertEquals(idField.fieldTypes.head.count, 200L)
    assertEquals(idField.fieldTypes.head.percentageOfParent, 1.0)
  }

  test("detect Json Schema from document dao") {
    // #region schema-explorer
    val schemaExplorer = new SchemaExplorer()
    val schema         = schemaExplorer.detectSchema(PersonDocumentDAO)
    val schemaJson     = schema.toJson
    // #endregion schema-explorer
    assert(schemaJson.contains("\"$schema\":\"https://json-schema.org/draft/2020-12/schema\""))
    assert(schemaJson.contains("\"Friends\":"))
    assert(schemaJson.contains("\"title\":\"Friends\""))
    assert(schemaJson.contains("\"People\":"))
    assert(schemaJson.contains("\"title\":\"People\""))
    val idPattern1 = schemaJson.contains("\"_id\":{\"pattern\":\"^([a-fA-F0-9]{2})+$\",\"type\":\"string\"}")
    val idPattern2 = schemaJson.contains("\"_id\":{\"type\":\"string\",\"pattern\":\"^([a-fA-F0-9]{2})+$\"}")
    assert(idPattern1 || idPattern2)
    assert(schemaJson.contains("\"isActive\":{\"type\":\"boolean\"}"))
  }
}
