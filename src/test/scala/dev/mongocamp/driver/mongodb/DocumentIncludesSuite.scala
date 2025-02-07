package dev.mongocamp.driver.mongodb

import dev.mongocamp.driver.DocumentIncludes
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import munit.FunSuite
import org.mongodb.scala.Document
import org.bson.types.ObjectId
import org.apache.lucene.search.MatchAllDocsQuery

class DocumentIncludesSuite extends FunSuite with DocumentIncludes {

  test("mapToBson should convert Map to Bson") {
    val map  = Map("key" -> "value")
    val bson = mapToBson(map)
    assert(bson.isInstanceOf[Document])
    assertEquals(bson.toBsonDocument.toJson(), "{\"key\": \"value\"}")
    assertEquals(bson.asInstanceOf[Document].getString("key"), "value")
  }

  test("luceneQueryBson should convert Lucene Query to Bson") {
    val query = new MatchAllDocsQuery()
    val bson  = luceneQueryBson(query)
    assert(bson.isInstanceOf[Document])
  }

  test("documentFromJavaMap should convert java.util.Map to Document") {
    val javaMap = new java.util.HashMap[String, Any]()
    javaMap.put("key", "value")
    val document = documentFromJavaMap(javaMap)
    assert(document.isInstanceOf[Document])
    assertEquals(document.toBsonDocument.toJson(), "{\"key\": \"value\"}")
    assertEquals(document.getString("key"), "value")
  }

  test("documentFromMutableMap should convert mutable.Map to Document") {
    val mutableMap: collection.mutable.Map[String, Any] = collection.mutable.Map("key" -> "value")
    val document                                        = documentFromMutableMap(mutableMap)
    assert(document.isInstanceOf[Document])
    assertEquals(document.toBsonDocument.toJson(), "{\"key\": \"value\"}")
    assertEquals(document.getString("key"), "value")
  }

  test("documentFromScalaMap should convert Map to Document") {
    val map      = Map("key" -> "value")
    val document = documentFromScalaMap(map)
    assert(document.isInstanceOf[Document])
    assertEquals(document.toBsonDocument.toJson(), "{\"key\": \"value\"}")
    assertEquals(document.getString("key"), "value")
  }

  test("documentFromDocument should convert org.bson.Document to Document") {
    val bsonDoc  = new org.bson.Document("key", "value")
    val document = documentFromDocument(bsonDoc)
    assert(document.isInstanceOf[Document])
    assertEquals(document.toBsonDocument.toJson(), "{\"key\": \"value\"}")
    assertEquals(document.getString("key"), "value")
  }

  test("mapFromDocument should convert Document to Map") {
    val document = Document("key" -> "value")
    val map      = mapFromDocument(document)
    assert(map.isInstanceOf[Map[_, _]])
    assertEquals(map("key"), "value")
  }

  test("mapListFromDocuments should convert List of Documents to List of Maps") {
    val documents = List(Document("key" -> "value"))
    val mapList   = mapListFromDocuments(documents)
    assert(mapList.isInstanceOf[List[_]])
    assertEquals(mapList.head("key"), "value")
  }

  test("stringToObjectId should convert String to ObjectId") {
    val str      = "507f1f77bcf86cd799439011"
    val objectId = stringToObjectId(str)
    assert(objectId.isInstanceOf[ObjectId])
    assertEquals(objectId.toHexString, str)
  }

  test("documentToObjectId should extract ObjectId from Document") {
    val objectId          = new ObjectId()
    val document          = Document(DatabaseProvider.ObjectIdKey -> objectId)
    val extractedObjectId = documentToObjectId(document)
    assertEquals(extractedObjectId, objectId)
  }
}
