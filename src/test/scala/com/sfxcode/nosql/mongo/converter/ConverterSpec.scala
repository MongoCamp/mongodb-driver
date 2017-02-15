package com.sfxcode.nosql.mongo.converter

import com.sfxcode.nosql.mongo.Converter
import com.sfxcode.nosql.mongo.json4s.DefaultBsonSerializer._
import org.json4s.Formats
import org.specs2.mutable.Specification

import scala.reflect.Manifest

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
