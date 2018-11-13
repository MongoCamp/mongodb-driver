package com.sfxcode.nosql.mongo.bson.codecs

import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.{BsonReader, BsonWriter}

/**
  * A Codec for BigInt instances.
  *
  * @since 3.0
  */
class BigIntCodec extends Codec[BigInt] {

  override def decode(reader: BsonReader,
                      decoderContext: DecoderContext): BigInt =
    BigInt(reader.readInt64())

  override def encode(writer: BsonWriter,
                      value: BigInt,
                      encoderContext: EncoderContext): Unit = {
    writer.writeInt64(value.toLong)
  }

  override def getEncoderClass: Class[BigInt] = classOf[BigInt]
}
