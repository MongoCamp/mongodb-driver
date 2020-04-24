package com.sfxcode.nosql.mongo.dao

import better.files.{File, Resource}
import com.sfxcode.nosql.mongo.TestDatabase._
import com.sfxcode.nosql.mongo._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAll

abstract class PersonSpecification extends Specification with BeforeAll {

  sequential

  override def beforeAll(): Unit = {
    PersonDAO.drop().result()
    PersonDAO.importJsonFile(File(Resource.getUrl("json/people.json"))).result()
  }

}
