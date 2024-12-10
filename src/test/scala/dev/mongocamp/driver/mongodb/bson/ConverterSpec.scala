package dev.mongocamp.driver.mongodb.bson

import dev.mongocamp.driver.mongodb.Converter
import org.specs2.mutable.Specification

import scala.reflect.ClassTag

/** Created by tom on 22.01.17.
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

    // todo: check if this is correct (test docuemnt)

    value must not be null

    value must haveClass[A]
  }

}
