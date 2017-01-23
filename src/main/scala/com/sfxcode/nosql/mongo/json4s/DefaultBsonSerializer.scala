package com.sfxcode.nosql.mongo.json4s

import org.json4s.{ DefaultFormats, Serializer }

/**
 * Created by tom on 19.01.17.
 */
trait DefaultBsonSerializer {

  implicit val formats = new DefaultFormats {

    override val customSerializers: List[Serializer[_]] = BsonSerializer.list
  }

}

object DefaultBsonSerializer extends DefaultBsonSerializer {

}

object BsonSerializer {
  val list = List(new ObjectIdSerializer(), new LongSerializer(), new BigIntSerializer(),
    new DateSerializer(), new DateTimeSerializer(), new LocalDateSerializer(), new LocalDateTimeSerializer())
}