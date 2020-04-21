package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import com.sfxcode.nosql.mongo.model.{ CodecTest, Line }
import org.specs2.mutable.Specification

class CrudSpec extends Specification {

  sequential

  "Crud Operations" should {

    "create Documents in" in {
      LineDAO.drop().result()
      LineDAO.insertOne(Line.line()).result()
      LineDAO.count().result() must be equalTo 1

    }

    "update Doduments" in {
      LineDAO.drop().result()
      LineDAO.insertOne(Line.line()).result()
      LineDAO.count().result() must be equalTo 1

      val line: Line = LineDAO.find()

      line.name must be equalTo "default"

      line.name = "test"
      LineDAO.replaceOne(line).result()

      val line2 = LineDAO.find().result()
      line2.name must be equalTo "test"

    }

    "delete Documents in" in {
      LineDAO.drop().result()
      LineDAO.insertOne(Line.line(2)).result()
      LineDAO.count().result() must be equalTo 1
      val line = LineDAO.find(Map("id" -> 2)).result()
      LineDAO.deleteOne(line).result()
      LineDAO.count().result() must be equalTo 0

    }

    "create Documents in" in {
      CodecDao.drop().result()
      CodecDao.insertOne(CodecTest()).result()
      CodecDao.count().result() must be equalTo 1
      val findOneResult = CodecDao.find("id", 1).resultOption()
      findOneResult must beSome[CodecTest]
    }

  }

}
