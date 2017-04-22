package com.sfxcode.nosql.mongo.bson.codecs

import org.bson.codecs.{ Codec, DecoderContext, EncoderContext }
import org.bson.{ BsonReader, BsonWriter }

/**
 * A Codec for BigDecimal instances.
 *
 * @since 3.0
 */
class BigDecimalCodec extends Codec[BigDecimal] {

  override def decode(reader: BsonReader, decoderContext: DecoderContext): BigDecimal = BigDecimal(reader.readDouble())

  override def encode(writer: BsonWriter, value: BigDecimal, encoderContext: EncoderContext): Unit = {
    writer.writeDouble(value.doubleValue())
  }

  override def getEncoderClass: Class[BigDecimal] = classOf[BigDecimal]
}