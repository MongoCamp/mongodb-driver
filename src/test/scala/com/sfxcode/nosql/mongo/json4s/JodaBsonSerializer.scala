package com.sfxcode.nosql.mongo.json4s

import org.json4s.{ DefaultFormats, Serializer }

/**
 * Created by tom on 15.02.17.
 */
object JodaBsonSerializer {
  implicit val formats = new DefaultFormats {

    override val customSerializers: List[Serializer[_]] = BsonSerializer.list ++ List(new DateTimeSerializer())
  }

}
