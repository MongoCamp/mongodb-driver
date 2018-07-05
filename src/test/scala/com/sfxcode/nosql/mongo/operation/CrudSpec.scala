package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo.model.Line
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification

class CrudSpec extends Specification {

  sequential

  "Crud Operations" should {

    "create Documents in" in {
      LineDAO.drop().headResult()
      LineDAO.insertOne(Line.line()).headResult()
      LineDAO.count().headResult() must be equalTo 1

    }

    "update Doduments" in {
      LineDAO.drop().headResult()
      LineDAO.insertOne(Line.line()).headResult()
      LineDAO.count().headResult() must be equalTo 1

      val line: Line = LineDAO.find()

      line.name must be equalTo "default"

      line.name = "test"
      LineDAO.replaceOne(line).headResult()

      val line2 = LineDAO.find().headResult()
      line2.name must be equalTo "test"

    }

    "delete Documents in" in {
      LineDAO.drop().headResult()
      LineDAO.insertOne(Line.line(2)).headResult()
      LineDAO.count().headResult() must be equalTo 1
      val line = LineDAO.find(Map("id" -> 2)).headResult()
      LineDAO.deleteOne(line).headResult()
      LineDAO.count().headResult() must be equalTo 0

    }

  }

}
