package com.sfxcode.nosql.mongo.bson.codecs

import org.bson.codecs.Codec
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}

case class CustomCodecProvider() extends CodecProvider {

  val BigIntClass = classOf[BigInt]
  val BigDecimalClass = classOf[BigDecimal]

  // scalastyle:off null
  @SuppressWarnings(Array("unchecked"))
  def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] = {
    clazz match {
      case BigIntClass     => new BigIntCodec().asInstanceOf[Codec[T]]
      case BigDecimalClass => new BigDecimalCodec().asInstanceOf[Codec[T]]
      case _               => null
    }
  }
  // scalastyle:on null
}
