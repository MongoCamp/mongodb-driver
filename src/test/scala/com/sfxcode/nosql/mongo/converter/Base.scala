package com.sfxcode.nosql.mongo.converter

import java.util.Date

/**
 * Created by tom on 22.01.17.
 */
case class Base(int: Int, Long: Long, float: Float, double: Double, string: String, date: Date = new Date())

object Base {
  def apply(): Base = new Base(1, 2, 3, 4, "test")
}
