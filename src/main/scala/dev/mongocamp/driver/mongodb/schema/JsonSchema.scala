package dev.mongocamp.driver.mongodb.schema

import dev.mongocamp.driver.mongodb.json._
import dev.mongocamp.driver.mongodb.json.JsonConverter

case class JsonSchema(`$schema`: String, `$ref`: String, definitions: Map[String, JsonSchemaDefinition]) {
  def toJson: String = {
    new JsonConverter().toJson(this)
  }
}

object JsonSchema {
  def apply(objectName: String, definitions: Map[String, JsonSchemaDefinition]): JsonSchema = {
    JsonSchema("https://json-schema.org/draft/2020-12/schema", s"#/definitions/$objectName", definitions)
  }
}
