package com.sfxcode.nosql.mongo.demo.restaurant

import java.text.SimpleDateFormat

import com.sfxcode.nosql.mongo.demo.restaurant.RestaurantDemoDatabase._
import com.sfxcode.nosql.mongo.model.Person.fromJson
import org.json4s.DefaultFormats
import org.json4s.native.Serialization._

object RestaurantReader {

  implicit val formats: DefaultFormats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
  }

  val testData: Restaurant = read[Restaurant](fromJson("/restaurant.json"))

}
