package dev.mongocamp.driver.mongodb.bson

import better.files.Resource
import scala.io.Source

/** Created by tom on 22.01.17.
  */
class DocumentHelperSuite extends munit.FunSuite {
  test("DocumentHelper should create Document") {
    val lines    = Source.fromURL(Resource.getUrl("json/people.json")).getLines().toList
    val document = DocumentHelper.documentFromJsonString(lines.head)
    assert(document.isDefined)
  }

}
