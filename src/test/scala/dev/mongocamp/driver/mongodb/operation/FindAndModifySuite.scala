package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.model.Person
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Filters.exists
import org.mongodb.scala.model.FindOneAndReplaceOptions
import org.mongodb.scala.model.FindOneAndUpdateOptions
import org.mongodb.scala.model.ReturnDocument
import org.mongodb.scala.model.Updates.set

class FindAndModifySuite extends BasePersonSuite with LazyLogging {

  test("findOneAndUpdate returns document BEFORE update by default") {
    val original = PersonDAO.find().resultList().head
    val before   = PersonDAO.findOneAndUpdate(equal("guid", original.guid), set("favoriteFruit", "find-and-update-banana")).resultOption()

    assert(before.isDefined, "Should return the document that was found")
    assertEquals(before.get.favoriteFruit, original.favoriteFruit, "Default: document BEFORE update is returned")

    val updated = PersonDAO.find(equal("guid", original.guid)).result()
    assertEquals(updated.favoriteFruit, "find-and-update-banana")

    PersonDAO.updateOne(equal("guid", original.guid), set("favoriteFruit", original.favoriteFruit)).result()
  }

  test("findOneAndUpdate with ReturnDocument.AFTER returns updated document") {
    val original = PersonDAO.find().resultList().head
    val after = PersonDAO
      .findOneAndUpdate(equal("guid", original.guid), set("favoriteFruit", "after-peach"), FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER))
      .resultOption()

    assert(after.isDefined, "Should return the modified document")
    assertEquals(after.get.favoriteFruit, "after-peach", "AFTER mode: document AFTER update is returned")

    PersonDAO.updateOne(equal("guid", original.guid), set("favoriteFruit", original.favoriteFruit)).result()
  }

  test("findOneAndUpdate returns None when no document matches") {
    val result = PersonDAO.findOneAndUpdate(equal("guid", "non-existent-guid-xyz"), set("favoriteFruit", "mango")).resultOption()

    assertEquals(result, None, "Should return None when no document matched")
  }

  test("findOneAndDelete removes the document and returns it") {
    // Insert a temporary document to delete — use a fresh ObjectId to avoid duplicate-key errors
    val victim = PersonDAO.find().resultList().head.copy(guid = "fad-delete-target", _id = new ObjectId())
    PersonDAO.insertOne(victim).result()

    val deleted = PersonDAO.findOneAndDelete(equal("guid", "fad-delete-target")).resultOption()

    assert(deleted.isDefined, "Should return the deleted document")
    assertEquals(deleted.get.guid, "fad-delete-target")

    // Verify it was actually deleted
    val stillExists = PersonDAO.count(equal("guid", "fad-delete-target")).result()
    assertEquals(stillExists, 0L, "Document should be gone after findOneAndDelete")
  }

  test("findOneAndDelete returns None when no document matches") {
    val result = PersonDAO.findOneAndDelete(equal("guid", "non-existent-guid-abc")).resultOption()
    assertEquals(result, None)
  }

  test("findOneAndReplace replaces document and returns document BEFORE by default") {
    val original    = PersonDAO.find().resultList().head
    val replacement = original.copy(favoriteFruit = "replace-kiwi")

    val before = PersonDAO.findOneAndReplace(equal("guid", original.guid), replacement).resultOption()

    assert(before.isDefined, "Should return the original document")
    assertEquals(before.get.favoriteFruit, original.favoriteFruit, "Default: document BEFORE replace returned")

    val afterReplace = PersonDAO.find(equal("guid", original.guid)).result()
    assertEquals(afterReplace.favoriteFruit, "replace-kiwi")

    // restore
    PersonDAO.replaceOne(equal("guid", original.guid), original).result()
  }

  test("findOneAndReplace with ReturnDocument.AFTER returns new document") {
    val original    = PersonDAO.find().resultList().head
    val replacement = original.copy(favoriteFruit = "replace-after-grape")

    val after =
      PersonDAO.findOneAndReplace(equal("guid", original.guid), replacement, FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER)).resultOption()

    assert(after.isDefined)
    assertEquals(after.get.favoriteFruit, "replace-after-grape", "AFTER mode: new document returned")

    PersonDAO.replaceOne(equal("guid", original.guid), original).result()
  }

  test("findOneAndReplace returns None when no document matches") {
    val template = PersonDAO.find().resultList().head.copy(guid = "non-existent-xyz")
    val result   = PersonDAO.findOneAndReplace(equal("guid", "non-existent-xyz"), template).resultOption()
    assertEquals(result, None)
  }

  test("upsertOne inserts when no document matches the filter") {
    val newPerson   = PersonDAO.find().resultList().head.copy(guid = "upsert-new-person", _id = new ObjectId())
    val countBefore = PersonDAO.count(equal("guid", "upsert-new-person")).result()
    assertEquals(countBefore, 0L)

    PersonDAO.upsertOne(equal("guid", "upsert-new-person"), newPerson).result()

    val countAfter = PersonDAO.count(equal("guid", "upsert-new-person")).result()
    assertEquals(countAfter, 1L, "upsertOne should insert when no match")

    PersonDAO.deleteMany(equal("guid", "upsert-new-person")).result()
  }

  test("upsertOne replaces when a document already matches the filter") {
    val existing = PersonDAO.find().resultList().head.copy(guid = "upsert-existing", _id = new ObjectId())
    PersonDAO.insertOne(existing).result()

    val updated = existing.copy(favoriteFruit = "upsert-updated-mango")
    PersonDAO.upsertOne(equal("guid", "upsert-existing"), updated).result()

    val count = PersonDAO.count(equal("guid", "upsert-existing")).result()
    assertEquals(count, 1L, "upsertOne should not create a duplicate")

    val result = PersonDAO.find(equal("guid", "upsert-existing")).result()
    assertEquals(result.favoriteFruit, "upsert-updated-mango")

    PersonDAO.deleteMany(equal("guid", "upsert-existing")).result()
  }

}
