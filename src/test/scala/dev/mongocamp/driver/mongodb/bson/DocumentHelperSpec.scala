package dev.mongocamp.driver.mongodb.bson

import better.files.{File, Resource}
import org.specs2.mutable.Specification

/** Created by tom on 22.01.17.
  */
class DocumentHelperSpec extends Specification {

  sequential

  "DocumentHelper" should {

    "create Document" in {
      val lines = File(Resource.getUrl("json/people.json")).lines

      val document = DocumentHelper.documentFromJsonString(lines.head)

      document.isDefined must be equalTo(true)

    }
  }

}
