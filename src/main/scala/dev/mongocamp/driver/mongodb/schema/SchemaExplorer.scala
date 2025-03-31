package dev.mongocamp.driver.mongodb.schema

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.json.JsonConverter
import org.bson.conversions.Bson
import org.mongodb.scala.documentToUntypedDocument
import org.mongodb.scala.Document

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration.DurationInt

class SchemaExplorer {
  private val NameSeparator: String = "."
  private val FieldSplitter: String = "_/_"
  private val ArrayItemMark: String = "[]"
  private val KeyFieldType          = "$$t"
  private val ObjectName            = "xl"
  private val ArrayName             = "xa"
  private val ArrayElementText      = "[array element]"

  private case class PipelineStage(stage: String, value: Any)

  private def schemaAggregation(deepth: Int, sampleSize: Option[Int]): List[PipelineStage] = {
    val buffer = ArrayBuffer[PipelineStage]()
    buffer ++= sampleSize.map(
      size => PipelineStage("sample", Map("size" -> size))
    )

    buffer += PipelineStage("project", Map("_" -> processObject(deepth, 0, "$$ROOT", List()), "_id" -> 0))

    (0 to deepth).foreach(
      _ => {
        buffer += PipelineStage("unwind", Map("path" -> "$_", "preserveNullAndEmptyArrays" -> true))
        buffer += PipelineStage("replaceRoot", Map("newRoot" -> Map("$cond" -> List(Map("$eq" -> List("$_", null)), "$$ROOT", "$_"))))
      }
    )

    buffer ++=
      List(
        PipelineStage("project", Map("_" -> 0)),
        PipelineStage("project", Map("l" -> "$$REMOVE", "n" -> 1, "t" -> 1, "v" -> "$$REMOVE")),
        PipelineStage("group", Map("_id" -> Map("n" -> "$n", "t" -> "$t"), "c" -> Map("$sum" -> 1))),
        PipelineStage("facet", Map("bS" -> List(Map("$project" -> Map("_id" -> Map("n" -> "$_id.n", "sT" -> "bS", "t" -> "$_id.t"), "c" -> 1))))),
        PipelineStage("project", Map("data" -> Map("$concatArrays" -> List("$bS")))),
        PipelineStage("unwind", "$data"),
        PipelineStage("replaceRoot", Map("newRoot" -> "$data")),
        PipelineStage("group", new JsonConverter().readJsonMapFromFile("schema/schema_stage11_group.json")),
        PipelineStage("replaceRoot", Map("newRoot" -> Map("$mergeObjects" -> "$S"))),
        PipelineStage("sort", Map("t" -> 1)),
        PipelineStage("group", Map("T" -> Map("$push" -> "$$ROOT"), "_id" -> Map("n" -> "$n"), "c" -> Map("$sum" -> "$c"))),
        PipelineStage("project", Map("T" -> 1, "_id" -> 0, "c" -> 1, "n" -> "$_id.n")),
        PipelineStage("sort", Map("n" -> 1))
      )
    buffer.toList
  }

  private case class AggregationField(name: String, value: String, level: Int)

  private def createBranch(`case`: Bson, `then`: Bson): Bson = Map("case" -> `case`, "then" -> `then`)

  private def createLet(in: Bson, vars: Bson): Bson = Map("$let" -> Map("in" -> in, "vars" -> vars))

  private def fieldValue(fieldName: String, fieldLevel: Int) = {
    Map("_" -> null, "e" -> fieldLevel, "n" -> generateFieldName(fieldName), "t" -> KeyFieldType)
  }

  private def generateFieldName(fieldName: String): Any = {
    val field = fieldName
      .replace("$$", "")
      .replace(ObjectName, FieldSplitter ++ ObjectName)
      .replace(ArrayName, FieldSplitter + ArrayName)
      .replace(ArrayItemMark, FieldSplitter + ArrayItemMark)
    val fields = field
      .split(FieldSplitter)
      .filterNot(
        s => s == null || s.isEmpty || s.isBlank
      )
    val responseArray: ArrayBuffer[String] = ArrayBuffer()
    fields.toList
      .map(
        string => string.replace(ObjectName, "$$" ++ ObjectName)
      )
      .foreach(
        string => {
          var fieldName = string
          if (fieldName.startsWith(NameSeparator)) {
            responseArray += NameSeparator
            fieldName = fieldName.substring(1)
          }
          val hasEndingSeperator: Boolean = if (fieldName.endsWith(NameSeparator)) {
            fieldName = fieldName.substring(0, fieldName.length - 1)
            true
          }
          else {
            false
          }
          responseArray += fieldName
          if (hasEndingSeperator) {
            responseArray += NameSeparator
          }
        }
      )

    if (responseArray.size == 1) {
      var result = responseArray.head
      if (!result.startsWith("$$")) {
        result = "$$%s".format(result)
      }
      result
    }
    else {
      Map("$concat" -> responseArray.toList)
    }
  }

  private def processField(maxLevel: Int, field: AggregationField, parents: List[String]): Bson = {
    val newParents       = addToParents(parents, field.name)
    val fullName: String = if (parents.isEmpty) field.name else newParents.mkString
    val stringBranch     = createBranch(Map("$eq" -> List(KeyFieldType, "string")), fieldValue(fullName, field.level))
    val arrayBranch      = createBranch(Map("$eq" -> List(KeyFieldType, "array")), processArrayField(maxLevel, fullName, field, parents))
    val objectBranch     = createBranch(Map("$eq" -> List(KeyFieldType, "object")), processObjectField(maxLevel, fullName, field, parents))
    Map("$switch" -> Map("branches" -> List(stringBranch, arrayBranch, objectBranch), "default" -> fieldValue(fullName, field.level)))
  }

  private def processArrayField(maxLevel: Int, fullName: String, field: AggregationField, parents: List[String]): Bson = {
    val nestedObject = if (field.level >= maxLevel) {
      null
    }
    else {
      Map("$concatArrays" -> List(List(null), processArray(maxLevel, field, parents)))
    }
    Map("_" -> nestedObject, "n" -> generateFieldName(fullName), "t" -> KeyFieldType, "e" -> field.level)
  }

  private def processArray(maxLevel: Int, field: AggregationField, parents: List[String]): Bson = {
    val level   = field.level
    val itemVar = s"$ArrayName$level"
    val item    = AggregationField(ArrayItemMark, itemVar, level + 1)
    Map(
      "$map" -> Map(
        "as"    -> itemVar,
        "in"    -> createLet(processField(maxLevel, item, addToParents(parents, field.name)), createTypeField(item)),
        "input" -> generateFieldName(field.value)
      )
    )
  }

  private def addToParents(list: List[String], newElement: String): List[String] = {
    if (!list.contains(newElement) || newElement.equalsIgnoreCase(ArrayItemMark)) {
      if (list.isEmpty) {
        List(newElement)
      }
      else {
        list ++ List(NameSeparator, newElement)
      }
    }
    else {
      list
    }
  }

  private def processObjectField(maxLevel: Int, fullName: String, field: AggregationField, parents: List[String]): Bson = {
    val nestedObject = if (field.level >= maxLevel) {
      null
    }
    else {
      Map("$concatArrays" -> List(List(null), processObject(maxLevel, field.level + 1, field.value, addToParents(parents, field.name))))
    }
    Map("_" -> nestedObject, "n" -> generateFieldName(fullName), "t" -> KeyFieldType, "e" -> field.level)
  }

  private def processObject(maxLevel: Int, level: Int, objectName: String, parents: List[String]): Bson = {
    val itemVar            = s"$ObjectName$level"
    val field              = AggregationField(s"$itemVar.k", s"$itemVar.v", level)
    val objectNameFunction = if (objectName.startsWith("$$")) objectName else "$$" + objectName
    Map(
      "$map" -> Map(
        "as"    -> itemVar,
        "in"    -> createLet(processField(maxLevel, field, parents), createTypeField(field)),
        "input" -> Map("$objectToArray" -> objectNameFunction)
      )
    )
  }

  private def createTypeField(field: AggregationField): Map[String, Any] = {
    val fieldValue = if (field.value.startsWith("$$")) field.value else "$$" + field.value
    Map("t" -> Map("$type" -> fieldValue))
  }

  private val emptyField = SchemaAnalysisField("ROOT", "", List(), -1, -1, ArrayBuffer())

  private def fieldsToJsonSchemaDefinition(map: mutable.Map[String, JsonSchemaDefinition], objectName: String, fields: List[SchemaAnalysisField]): Unit = {
    map.put(objectName, null)
    val requiredFields = ArrayBuffer[String]()
    val properties     = mutable.Map[String, Map[String, Any]]()
    fields.distinct.foreach(
      field => {
        val fieldMap        = mutable.Map[String, Any]()
        val fieldObjectName = getObjectName(camelCaseObjectName(field.name), map)
        if (field.fieldTypes.exists(_.fieldType.equalsIgnoreCase("object"))) {
          fieldsToJsonSchemaDefinition(map, fieldObjectName, field.subFields.toList)
        }
        if (field.percentageOfParent == 1.0) {
          requiredFields += field.name
        }
        if (field.fieldTypes.size == 1) {
          val t                  = field.fieldTypes.head
          val convertedFieldType = convertFieldType(t.fieldType)
          fieldMap.put("type", convertedFieldType.name)
          convertedFieldType.pattern.foreach(
            value => fieldMap.put("pattern", value)
          )
          convertedFieldType.format.foreach(
            value => fieldMap.put("format", value)
          )
          val mapping: Map[String, Any] = if (t.fieldType.equalsIgnoreCase("array")) {
            val items = {
              val subField = field.subFields.head
              if (subField.fieldTypes.size == 1) {
                if (subField.fieldTypes.head.fieldType.equalsIgnoreCase("object")) {
                  fieldsToJsonSchemaDefinition(map, fieldObjectName, subField.subFields.toList)
                  Map("$ref" -> s"#/definitions/$fieldObjectName")
                }
                else {
                  val convertedFieldType = convertFieldType(subField.fieldTypes.head.fieldType)
                  val mutableMap         = mutable.Map[String, Any]()
                  mutableMap.put("type", convertedFieldType.name)
                  convertedFieldType.pattern.foreach(
                    value => mutableMap.put("pattern", value)
                  )
                  convertedFieldType.format.foreach(
                    value => mutableMap.put("format", value)
                  )
                  mutableMap.toMap
                }
              }
              else {
                Map("oneOf" -> getOneOfMapping(field, fieldObjectName, map))
              }
            }
            Map("type" -> "array", "items" -> items)
          }
          else if (t.fieldType.equalsIgnoreCase("object")) {
            fieldsToJsonSchemaDefinition(map, fieldObjectName, field.subFields.toList)
            Map("$ref" -> s"#/definitions/$fieldObjectName")
          }
          else {
            val convertedFieldType = convertFieldType(t.fieldType)
            val mutableMap         = mutable.Map[String, Any]()
            mutableMap.put("type", convertedFieldType.name)
            convertedFieldType.pattern.foreach(
              value => mutableMap.put("pattern", value)
            )
            convertedFieldType.format.foreach(
              value => mutableMap.put("format", value)
            )
            mutableMap.toMap
          }
          mapping.foreach(
            element => fieldMap.put(element._1, element._2)
          )
        }
        else {
          fieldMap.put("oneOf", getOneOfMapping(field, fieldObjectName, map))
        }
        properties.put(field.name, fieldMap.toMap)
      }
    )
    val jsonSchemaDefinition = JsonSchemaDefinition("object", objectName, additionalProperties = false, requiredFields.toList, properties.toMap)
    map.put(objectName, jsonSchemaDefinition)
  }

  private case class JsonSchemaFieldType(name: String, pattern: Option[String] = None, format: Option[String] = None)

  private def convertFieldType(fieldType: String): JsonSchemaFieldType = {
    val numberTypes     = List("double", "float")
    val fullNumberTypes = List("int", "long")
    if (fieldType.equalsIgnoreCase("objectId")) {
      JsonSchemaFieldType("string", Some("^([a-fA-F0-9]{2})+$"))
    }
    else if (fieldType.equalsIgnoreCase("bool")) {
      JsonSchemaFieldType("boolean")
    }
    else if (fieldType.equalsIgnoreCase("date")) {
      JsonSchemaFieldType("string", None, Some("date-time"))
    }
    else if (fullNumberTypes.exists(_.equalsIgnoreCase(fieldType))) {
      JsonSchemaFieldType("integer")
    }
    else if (numberTypes.exists(_.equalsIgnoreCase(fieldType))) {
      JsonSchemaFieldType("number")
    }
    else {
      JsonSchemaFieldType(fieldType)
    }
  }

  private def getOneOfMapping(field: SchemaAnalysisField, fieldObjectName: String, map: mutable.Map[String, JsonSchemaDefinition]) = {
    field.fieldTypes
      .map(
        t => {
          if (t.fieldType.equalsIgnoreCase("array")) {
            if (t.fieldType.equalsIgnoreCase("object")) {
              fieldsToJsonSchemaDefinition(map, fieldObjectName, field.subFields.toList)
              Map("type" -> "array", "items" -> Map("$ref" -> s"#/definitions/$fieldObjectName"))
            }
            else {
              val arrayItemType      = field.subFields.find(_.name.equalsIgnoreCase(ArrayElementText)).map(_.fieldTypes.head.fieldType).getOrElse("Error")
              val convertedFieldType = convertFieldType(arrayItemType)
              val mutableMap         = mutable.Map[String, Any]()
              mutableMap.put("type", convertedFieldType.name)
              convertedFieldType.pattern.foreach(
                value => mutableMap.put("pattern", value)
              )
              convertedFieldType.format.foreach(
                value => mutableMap.put("format", value)
              )
              mutableMap.toMap
            }
          }
          else if (t.fieldType.equalsIgnoreCase("object")) {
            fieldsToJsonSchemaDefinition(map, fieldObjectName, field.subFields.toList)
            Map("$ref" -> s"#/definitions/$fieldObjectName")
          }
          else {
            val convertedFieldType = convertFieldType(t.fieldType)
            val mutableMap         = mutable.Map[String, Any]()
            mutableMap.put("type", convertedFieldType.name)
            convertedFieldType.pattern.foreach(
              value => mutableMap.put("pattern", value)
            )
            convertedFieldType.format.foreach(
              value => mutableMap.put("format", value)
            )
            mutableMap.toMap
          }
        }
      )
      .distinct
  }

  private def getObjectName(objectName: String, map: mutable.Map[String, JsonSchemaDefinition]): String = {
    val name = if (map.exists(_._1.equals(objectName))) {
      val nameCounterString = objectName.split("_").last
      val count =
        try nameCounterString.toLong
        catch {
          case nF: NumberFormatException => 0
        }
      val nameCounter: Long = if (count.toString.equalsIgnoreCase(nameCounterString)) {
        count + 1
      }
      else {
        1
      }
      getObjectName(s"${objectName}_$nameCounter", map)
    }
    else {
      objectName
    }
    name
  }

  private def camelCaseObjectName(objectName: String) = {
    objectName.split("[^a-zA-Z]").map(_.capitalize).mkString.trim
  }

  private def convertToBsonPipeline(pipeline: List[PipelineStage]): Seq[Bson] = {
    val response: Seq[Bson] = pipeline.map(
      element => {
        val stage      = if (element.stage.startsWith("$")) element.stage else "$" + element.stage
        val bson: Bson = Map(stage -> element.value)
        bson
      }
    )
    response
  }

  def analyzeSchema(dao: MongoDAO[Document], deepth: Int = 10, sample: Option[Int] = None): SchemaAnalysis = {
    val dbResponse    = dao.findAggregated(convertToBsonPipeline(schemaAggregation(deepth, sample)), allowDiskUse = true).resultList(3.minutes.toSeconds.toInt)
    val countResponse = dao.count().result()
    val sampledDataCountOption: Option[Long] = dbResponse
      .find(
        document => document.getString("n").equalsIgnoreCase("_id")
      )
      .map(_.getLongValue("c"))
    val sampledDataCount = sampledDataCountOption.getOrElse(-1L)
    val fieldsMap        = mutable.Map[String, SchemaAnalysisField]()
    fieldsMap.put(emptyField.name, emptyField.copy(count = sampledDataCount))
    dbResponse.foreach(
      document => {
        val documentMap        = mapFromDocument(document)
        val fullName           = documentMap.get("n").map(_.toString).getOrElse("")
        var name: String       = fullName
        var parentName: String = emptyField.name
        var percentage: Double = 0
        val fieldCount         = document.getLongValue("c")

        if (fullName.contains(NameSeparator)) {
          val fieldNames = fullName.split(NameSeparator.charAt(0))
          name = fieldNames.last
          val parentFields = fieldNames.splitAt(fieldNames.length - 1)
          parentName = parentFields._1.mkString(".")
        }
        else {
          percentage = fieldCount / sampledDataCount.toDouble
        }

        val parent = fieldsMap.getOrElse(
          parentName, {
            val newF = emptyField.copy(name = parentName)
            fieldsMap.put(parentName, newF)
            newF
          }
        )

        if (fullName.contains(NameSeparator)) {
          percentage = fieldCount.toDouble / parent.count.toDouble
        }
        val types: List[SchemaAnalysisFieldType] = documentMap
          .get("T")
          .map(_.asInstanceOf[List[Map[String, Any]]])
          .getOrElse(List())
          .map(
            typeDocument => {
              val doc                         = documentFromScalaMap(typeDocument)
              val count                       = doc.getLongValue("c")
              val fieldTypePercentage: Double = count.toDouble / parent.count.toDouble
              SchemaAnalysisFieldType(doc.getStringValue("t"), count, fieldTypePercentage)
            }
          )

        val newField = SchemaAnalysisField(name.replace(ArrayItemMark, ArrayElementText), fullName, types, fieldCount, percentage, ArrayBuffer())

        parent.subFields.+=(newField)
        fieldsMap.put(s"$parentName$NameSeparator$name".replace("ROOT.", ""), newField)
      }
    )

    val fieldPercentage: Double = if (countResponse != 0) sampledDataCount / countResponse else 0
    SchemaAnalysis(countResponse, sampledDataCount, fieldPercentage, fieldsMap.get("ROOT").map(_.subFields).getOrElse(ArrayBuffer()))
  }

  def detectSchema(dao: MongoDAO[Document], deepth: Int = 10, sample: Option[Int] = None): JsonSchema = {
    val objectName = camelCaseObjectName(dao.collection.namespace.getCollectionName)
    val analysis   = analyzeSchema(dao, deepth, sample)
    val map        = mutable.Map[String, JsonSchemaDefinition]()
    fieldsToJsonSchemaDefinition(map, objectName, analysis.fields.toList)
    JsonSchema(objectName, map.toMap)
  }

}
