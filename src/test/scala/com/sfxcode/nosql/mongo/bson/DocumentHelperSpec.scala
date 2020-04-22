package com.sfxcode.nosql.mongo.bson

import better.files.{ File, Resource }
import com.sfxcode.nosql.mongo.Converter
import org.specs2.mutable.Specification

import scala.reflect.ClassTag

/**
 * Created by tom on 22.01.17.
 */
class DocumentHelperSpec extends Specification {

  sequential

  "DocumentHelper" should {

    "create Document" in {
      val lines = File(Resource.getUrl("json/people.json")).lines

      val document = DocumentHelper.documentFromJsonString(lines.head)

      document must beSome()

    }
  }

}
