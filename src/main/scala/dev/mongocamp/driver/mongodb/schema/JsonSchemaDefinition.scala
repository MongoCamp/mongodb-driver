package dev.mongocamp.driver.mongodb.schema

case class JsonSchemaDefinition(
    `type`: String,
    title: String,
    additionalProperties: Boolean,
    required: List[String],
    properties: Map[String, Map[String, Any]]
)
