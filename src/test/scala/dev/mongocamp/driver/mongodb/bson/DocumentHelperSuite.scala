package dev.mongocamp.driver.mongodb.bson

import better.files.{ File, Resource }

/** Created by tom on 22.01.17.
  */
class DocumentHelperSuite extends munit.FunSuite {
  test("DocumentHelper should create Document") {
    val lines = File(Resource.getUrl("json/people.json")).lines

    val document = DocumentHelper.documentFromJsonString(lines.head)

    assert(document.isDefined)
  }

}
