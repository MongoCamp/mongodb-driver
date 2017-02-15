package com.sfxcode.nosql.mongo.json4s

import org.json4s.{ DefaultFormats, Serializer }

object DefaultBsonSerializer {
  implicit val formats = new DefaultFormats {

    override val customSerializers: List[Serializer[_]] = BsonSerializer.list
  }
}

object BsonSerializer {
  val list = List(new ObjectIdSerializer(), new LongSerializer(), new BigIntSerializer(),
    new DateSerializer(), new LocalDateSerializer(), new LocalDateTimeSerializer())
}