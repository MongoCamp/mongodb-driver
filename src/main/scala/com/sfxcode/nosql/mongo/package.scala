package com.sfxcode.nosql

import com.sfxcode.nosql.mongo.operation.ObservableIncludes
import org.mongodb.scala.bson.conversions.Bson

package object mongo extends ObservableIncludes {

  implicit def mapToBson(value: Map[_, _]): Bson = Converter.toDocument(value)

}