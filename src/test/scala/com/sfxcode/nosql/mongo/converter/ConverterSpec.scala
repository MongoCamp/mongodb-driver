package com.sfxcode.nosql.mongo.converter

import com.sfxcode.nosql.mongo.Converter
import org.specs2.mutable.Specification

import scala.reflect.ClassTag

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

  def roundtrip[A <: AnyRef](value: A)(implicit ct: ClassTag[A]): Unit = {
    val document = Converter.toDocument(value)

    value must not beNull

    value must haveClass[A]
  }

}
