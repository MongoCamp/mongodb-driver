package dev.mongocamp.driver.mongodb.schema

import scala.collection.mutable.ArrayBuffer

case class SchemaAnalysisField (
                                 name: String,
                                 fullName: String,
                                 fieldTypes: List[SchemaAnalysisFieldType],
                                 count: Long,
                                 percentageOfParent: Double,
                                 subFields: ArrayBuffer[SchemaAnalysisField]
                               )
