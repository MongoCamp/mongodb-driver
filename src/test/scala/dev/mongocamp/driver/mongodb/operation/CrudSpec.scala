package dev.mongocamp.driver.mongodb.operation

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.database.DatabaseProvider
import dev.mongocamp.driver.mongodb.model.CodecTest
import dev.mongocamp.driver.mongodb.test.TestDatabase._
import com.typesafe.scalalogging.LazyLogging
import org.bson.conversions.Bson
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.result.DeleteResult
import org.mongodb.scala.model.Updates._

class CrudSpec extends PersonSpecification with LazyLogging {

  sequential

  override def beforeAll(): Unit = {
    super.beforeAll()
    CodecDao.drop().result()

  }

  "Crud Operations" should {

    "create Document in" in {
      val result = CodecDao.insertOne(CodecTest()).result()

      val list: List[CodecTest] = CodecDao.find().resultList()
      list.size mustEqual 1

    }

    "update Document in" in {
      var list: List[CodecTest] = CodecDao.find().resultList()
      var codec                 = list.head
      codec.id mustEqual 1
      CodecDao.updateOne(Map("id" -> 1), set("id", 2)).result()
      list = CodecDao.find().resultList()
      codec = list.head
      codec.id mustEqual 2
    }

    "replace Document in" in {
      var list: List[CodecTest] = CodecDao.find().resultList()
      var codec                 = list.head
      codec.id mustEqual 2
      CodecDao.replaceOne(codec.copy(id = 1)).result()
      list = CodecDao.find().resultList()
      codec = list.head
      codec.id mustEqual 1
    }

    "delete Document in" in {
      val hexString = CodecDao.find().result()._id.toHexString
      hexString must not be null
      (hexString must not).equalTo("")

      val result: DeleteResult =
        CodecDao.deleteOne(equal(DatabaseProvider.ObjectIdKey, new ObjectId(hexString))).result()

      result.wasAcknowledged() must beTrue
      result.getDeletedCount must beEqualTo(1)

      val list: List[CodecTest] = CodecDao.find().resultList()

      list.size mustEqual 0
    }

  }

}
