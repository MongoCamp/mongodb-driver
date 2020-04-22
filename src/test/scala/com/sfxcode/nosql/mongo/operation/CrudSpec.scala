package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.dao.PersonSpecification
import com.sfxcode.nosql.mongo.database.ChangeObserver
import com.sfxcode.nosql.mongo.model.CodecTest
import com.typesafe.scalalogging.LazyLogging
import org.mongodb.scala.model.changestream.ChangeStreamDocument

class CrudSpec extends PersonSpecification with LazyLogging {

  sequential

  override def beforeAll(): Unit = {
    super.beforeAll()
    CodecDao.drop().result()
    // CodecDao.addChangeObserver(ChangeObserver(consumeCodecChanges))
    CodecDao.insertOne(CodecTest()).result()

    def consumeCodecChanges(changeStreamDocument: ChangeStreamDocument[CodecTest]): Unit =
      logger.info(
        "codec changed %s:%s with ID: %s".format(changeStreamDocument.getNamespace,
                                                 changeStreamDocument.getOperationType,
                                                 changeStreamDocument.getDocumentKey)
      )

  }

  "Crud Operations" should {

    "create Documents in" in {

      CodecDao.count().result() must be equalTo 1
      val findOneResult = CodecDao.find("id", 1).resultOption()
      findOneResult must beSome[CodecTest]
    }

  }

}
