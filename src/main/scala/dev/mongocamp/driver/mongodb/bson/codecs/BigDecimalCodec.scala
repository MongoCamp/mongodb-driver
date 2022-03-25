package dev.mongocamp.driver.mongodb.bson.codecs

import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonWriter}

/** A Codec for BigDecimal instances.
  */
class BigDecimalCodec extends Codec[BigDecimal] {

  override def decode(reader: BsonReader, decoderContext: DecoderContext): BigDecimal =
    BigDecimal(reader.readDouble())

  override def encode(writer: BsonWriter, value: BigDecimal, encoderContext: EncoderContext): Unit =
    writer.writeDouble(value.toDouble)

  override def getEncoderClass: Class[BigDecimal] = classOf[BigDecimal]
}
