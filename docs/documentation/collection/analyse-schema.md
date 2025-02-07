# Analyse Schema
The driver supports an automated detection of the schema of an existing collection. The schema is used to detect the types of the columns. 

## Usage 

### Schema Analysis
Analyse a collection to detect the values for each field and the percentage distribution of the types. 

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/schema/SchemaSuite.scala#schema-analysis

### Detect Schema 
The Schema Detector can be used to detect the schema of a collection and is based on [Schema Anaysis](analyse-schema.md#schema-analysis).  The schema is used to detect the types of the columns and generate a [JSON Schema](https://json-schema.org) for the collection. In case of multiple types of a field the Generation of the JSON Schema use the type with the most elements. 

:::tip
The [JSON Schema](https://json-schema.org) format can be use to validate or generate data, as well to secure your [Mongo Collection](https://www.mongodb.com/docs/manual/core/schema-validation/).
:::

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/schema/SchemaSuite.scala#schema-explorer