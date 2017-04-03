package com.sfxcode.nosql.mongo.converter

import com.sfxcode.nosql.mongo.Converter
import org.specs2.mutable.Specification

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

  def roundtrip[A <: Any](value: A): Unit = {
    val document = Converter.toDocument(value)
    value must not beNull
  }

}
