package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo.model.Line
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification

class CrudSpec extends Specification {

  sequential

  "Crud Operations" should {

    "create Documents in" in {
      LineDAO.dropResult()
      LineDAO.insertOneResult(Line.line())
      LineDAO.countResult() must be equalTo 1

    }

    "update Doduments" in {
      LineDAO.dropResult()
      LineDAO.insertOneResult(Line.line())
      LineDAO.countResult() must be equalTo 1

      val line = LineDAO.find().headResult()

      line.name must be equalTo "default"

      line.name = "test"
      LineDAO.replaceOneResult(line)

      val line2 = LineDAO.find().headResult()
      line2.name must be equalTo "test"

    }

    "delete Documents in" in {
      LineDAO.dropResult()
      LineDAO.insertOneResult(Line.line(2))
      LineDAO.countResult() must be equalTo 1
      val line = LineDAO.find(Map("id" -> 2)).headResult()
      LineDAO.deleteOneResult(line)
      LineDAO.countResult() must be equalTo 0

    }

  }

}
