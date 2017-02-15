package com.sfxcode.nosql.mongo.operation

import com.sfxcode.nosql.mongo.tour.Database._
import com.sfxcode.nosql.mongo.model.Line
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification

/**
 * Created by tom on 22.01.17.
 */
class CrudSpec extends Specification {

  sequential

  "Crud Operations" should {

    "create Documents in" in {
      LineDAO.drop()
      LineDAO.insertResult(Line.line())
      LineDAO.count() must be equalTo 1

    }

    "update Doduments" in {
      LineDAO.drop()
      LineDAO.insertResult(Line.line())
      LineDAO.count() must be equalTo 1

      val line = LineDAO.findAll().head

      line.name must be equalTo "default"

      line.name = "test"
      LineDAO.updateResult(line)

      val line2 = LineDAO.findAll().head
      line2.name must be equalTo "test"

    }

    "delete Documents in" in {
      LineDAO.drop()
      LineDAO.insertResult(Line.line(2))
      LineDAO.count() must be equalTo 1
      val line = LineDAO.find(Map("id" -> 2)).head
      LineDAO.deleteResult(line)
      LineDAO.count() must be equalTo 0

    }

  }

}
