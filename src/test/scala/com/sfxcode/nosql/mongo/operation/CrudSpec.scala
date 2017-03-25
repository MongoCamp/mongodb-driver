package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.tour.Database._
import com.sfxcode.nosql.mongo.model.Line
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification

class CrudSpec extends Specification {

  sequential

  "Crud Operations" should {

    "create Documents in" in {
      LineDAO.dropResult()
      LineDAO.insertResult(Line.line())
      LineDAO.countResult() must be equalTo 1

    }

    "update Doduments" in {
      LineDAO.dropResult()
      LineDAO.insertResult(Line.line())
      LineDAO.countResult() must be equalTo 1

      val line = LineDAO.findAll().head

      line.name must be equalTo "default"

      line.name = "test"
      LineDAO.updateResult(line)

      val line2 = LineDAO.findAll().head
      line2.name must be equalTo "test"

    }

    "delete Documents in" in {
      LineDAO.dropResult()
      LineDAO.insertResult(Line.line(2))
      LineDAO.countResult() must be equalTo 1
      val line = LineDAO.find(Map("id" -> 2)).head
      LineDAO.deleteResult(line)
      LineDAO.countResult() must be equalTo 0

    }

  }

}
