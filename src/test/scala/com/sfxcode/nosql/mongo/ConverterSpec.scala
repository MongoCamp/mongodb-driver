package com.sfxcode.nosql.mongo

import com.sfxcode.nosql.mongo.converter.Base
import org.json4s.Formats
import org.specs2.mutable.Specification

import scala.reflect.Manifest
import com.sfxcode.nosql.mongo.json4s.DefaultBsonSerializer._

/**
 * Created by tom on 22.01.17.
 */
class ConverterSpec extends Specification {

  sequential

  "Converter" should {

    "support Document roundtrip" in {

      roundtrip[Base](Base())

      true must beTrue
    }
  }

  def roundtrip[A <: Any](value: A)(implicit formats: Formats, mf: Manifest[A]): Unit = {
    val document = Converter.toDocument(value)
    val valueFromDocument = Converter.fromDocument[A](document)
    value must be equalTo valueFromDocument
  }

}
