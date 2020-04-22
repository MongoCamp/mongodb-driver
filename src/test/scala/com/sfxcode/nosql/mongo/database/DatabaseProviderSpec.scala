package com.sfxcode.nosql.mongo.database

import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import org.mongodb.scala.Document
import org.specs2.mutable.Specification

class DatabaseProviderSpec extends Specification {

  "Base Operations" should {

    "must evaluate buildInfo" in {

      val result: Document = provider.runCommand(Map("buildInfo" -> 1)).result()

      result.getDouble("ok") mustEqual 1.0
    }

    "must evaluate collectionInfo" in {

      val result: Document = provider.runCommand(Map("collStats" -> "books")).result()

      val s = result.asPlainJson
      result must not beEmpty
    }
  }
}
