package dev.mongocamp.driver.mongodb.schema

import scala.collection.mutable.ArrayBuffer

case class SchemaAnalysis(count: Long, sample: Long, percentageOfAnalysed: Double, fields: ArrayBuffer[SchemaAnalysisField])
