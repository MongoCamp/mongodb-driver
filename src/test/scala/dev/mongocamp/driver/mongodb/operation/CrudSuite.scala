package dev.mongocamp.driver.mongodb.operation

import com.typesafe.scalalogging.LazyLogging
import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.model.CodecTest
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.DeleteResult

class CrudSuite extends BasePersonSuite with LazyLogging {

  override def beforeAll(): Unit = {
    super.beforeAll()
    CodecDao.drop().result()
  }

  test("create Document") {
    CodecDao.insertOne(CodecTest()).result()
    val list: List[CodecTest] = CodecDao.find().resultList()
    assertEquals(list.size, 1)
  }

  test("update Document") {
    var list: List[CodecTest] = CodecDao.find().resultList()
    var codec                 = list.head
    assertEquals(codec.id, 1L)
    CodecDao.updateOne(Map("id" -> 1), set("id", 2)).result()
    list = CodecDao.find().resultList()
    codec = list.head
    assertEquals(codec.id, 2L)
  }

  test("replace Document") {
    var list: List[CodecTest] = CodecDao.find().resultList()
    var codec                 = list.head
    assertEquals(codec.id, 2L)
    CodecDao.replaceOne(codec.copy(id = 1)).result()
    list = CodecDao.find().resultList()
    codec = list.head
    assertEquals(codec.id, 1L)
  }

  test("delete Document") {
    val hexString = CodecDao.find().result()._id.toHexString
    assert(hexString != null)
    assert(hexString != "")

    val result: DeleteResult = CodecDao.deleteOne(equal(DatabaseProvider.ObjectIdKey, new ObjectId(hexString))).result()

    assert(result.wasAcknowledged())
    assertEquals(result.getDeletedCount, 1L)

    val list: List[CodecTest] = CodecDao.find().resultList()
    assertEquals(list.size, 0)
  }

}
