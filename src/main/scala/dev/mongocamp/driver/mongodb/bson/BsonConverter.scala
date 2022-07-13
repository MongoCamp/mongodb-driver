package dev.mongocamp.driver.mongodb.bson

import org.mongodb.scala.Document
import org.mongodb.scala.bson.BsonArray.fromIterable
import org.mongodb.scala.bson.{ ObjectId, _ }

import java.math.BigInteger
import java.time.{ LocalDate, LocalDateTime, ZoneId }
import java.util.Date
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.util.matching.Regex

object BsonConverter {
  val DocumentKeyDivider = "."

  def hasRelation(key: String): Boolean = key.indexOf(DocumentKeyDivider) != -1

  def relationKey(key: String): String = key.substring(0, key.indexOf(DocumentKeyDivider))

  def newKeyFromRelation(key: String): String = key.substring(key.indexOf(DocumentKeyDivider) + 1)

  def lastKeyFromRelation(key: String): String = key.substring(key.lastIndexOf(DocumentKeyDivider) + 1)

  def documentValueOption(document: Document, key: String): Option[Any] =
    if (hasRelation(key)) {
      val newKey   = newKeyFromRelation(key)
      val relation = relationKey(key)

      if (document.contains(relation) && documentValueOption(document, relation).isDefined) {
        val value = documentValueOption(document, relation).get
        value match {
          case document: Document =>
            documentValueOption(document, newKey)
          case _ =>
            None
        }
      }
      else
        None
    }
    else if (document.contains(key))
      Some(fromBson(document(key)))
    else
      None

  def updateDocumentValue(document: Document, key: String, value: Any): Document = {
    val doc    = org.mongodb.scala.bson.collection.mutable.Document(document.toJson())
    val result = updateDocumentValueInternal(doc, key, value)
    Document(result.toJson())
  }

  private def updateDocumentValueInternal(
      document: org.mongodb.scala.bson.collection.mutable.Document,
      key: String,
      value: Any,
      root: Option[org.mongodb.scala.bson.collection.mutable.Document] = None
  ): org.mongodb.scala.bson.collection.mutable.Document =
    if (hasRelation(key)) {
      val newKey   = newKeyFromRelation(key)
      val relation = relationKey(key)

      var relatedDocument = Document()
      val relationValue   = documentValueOption(Document(document.toJson()), relation)
      if (relationValue.isDefined) {
        val value = relationValue.get
        value match {
          case document: Document =>
            relatedDocument = document
          case _ =>
        }
      }
      val mutableDoc = org.mongodb.scala.bson.collection.mutable.Document.apply(relatedDocument.toJson())
      document.put(relation, mutableDoc)
      if (root.isEmpty) {
        updateDocumentValueInternal(mutableDoc, newKey, value, Some(document))
      }
      else {
        updateDocumentValueInternal(mutableDoc, newKey, value, root)
      }

    }
    else {
      document.put(key, toBson(value))
      if (root.isEmpty) {
        document
      }
      else {
        root.get
      }
    }

  var converterPlugin: AbstractConverterPlugin = new BaseConverterPlugin()

  def toBson(value: Any): BsonValue =
    value match {
      case bsonValue: BsonValue => bsonValue
      case option: Option[Any] =>
        if (option.isDefined) {
          toBson(option.get)
        }
        else {
          BsonNull()
        }
      case v: Any if converterPlugin.hasCustomClass(v) =>
        converterPlugin.toBson(v)
      case b: Boolean         => BsonBoolean(b)
      case s: String          => BsonString(s)
      case c: Char            => BsonString(c.toString)
      case bytes: Array[Byte] => BsonBinary(bytes)
      case r: Regex           => BsonRegularExpression(r)
      case d: Date            => BsonDateTime(d)
      case ld: LocalDate =>
        BsonDateTime(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant))
      case ldt: LocalDateTime =>
        BsonDateTime(Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant))
      case oid: ObjectId            => BsonObjectId(oid)
      case i: Int                   => BsonInt32(i)
      case l: Long                  => BsonInt64(l)
      case bi: BigInt               => BsonInt64(bi.toLong)
      case bi: BigInteger           => BsonInt64(bi.longValue())
      case d: Double                => BsonDouble(d)
      case f: Float                 => BsonDouble(f)
      case bd: BigDecimal           => BsonDecimal128.apply(bd)
      case bd: java.math.BigDecimal => BsonDecimal128.apply(bd)
      case doc: Document            => BsonDocument(doc)
      case map: scala.collection.Map[_, _] =>
        var doc = Document()
        map.keys.foreach { key =>
          val v = map(key)
          doc.+=(key.toString -> toBson(v))
        }
        BsonDocument(doc)
      case map: java.util.Map[_, _] =>
        var doc = Document()
        map
          .keySet()
          .asScala
          .foreach { key =>
            val v = map.get(key)
            doc.+=(key.toString -> toBson(v))
          }
        BsonDocument(doc)
      case it: Iterable[Any] =>
        fromIterable(it.map(v => toBson(v)))
      case list: java.util.List[_] =>
        fromIterable(list.asScala.map(v => toBson(v)))
      case v: AnyRef => converterPlugin.objectToBson(v)
      case _ =>
        BsonNull()
    }

  def fromBson(value: BsonValue): Any =
    value match {

      case b: BsonBoolean           => b.getValue
      case s: BsonString            => s.getValue
      case bytes: BsonBinary        => bytes.getData
      case r: BsonRegularExpression => r.getPattern
      case d: BsonDateTime          => new Date(d.getValue)
      case d: BsonTimestamp         => new Date(d.getTime)
      case oid: BsonObjectId        => oid.getValue
      case i: BsonInt32             => i.getValue
      case l: BsonInt64             => l.getValue
      case d: BsonDouble            => d.doubleValue()
      case d: BsonDecimal128        => d.getValue.bigDecimalValue()
      case doc: BsonDocument        => Document(doc)
      case array: BsonArray =>
        array.getValues.asScala.toList.map(v => fromBson(v))
      case n: BsonNull => null
      case _           => value
    }

  def asMap(document: Document): Map[String, Any] = {
    val result = new mutable.HashMap[String, Any]()
    document.keySet.foreach { key =>
      val value = fromBson(document(key))

      value match {
        case d: Document =>
          result.+=(key -> asMap(d))
        case ld: List[Any] =>
          result.+=(key -> ld.map(d => {
            if (d.isInstanceOf[Document]) {
              asMap(d.asInstanceOf[Document])
            }
            else {
              d
            }
          }))
        case _ =>
          result.+=(key -> value)
      }
    }
    result.toMap
  }

  def asMapList(documents: List[Document]): List[Map[String, Any]] = {
    val result = new mutable.ArrayBuffer[Map[String, Any]]()
    documents.foreach(document => result.+=(asMap(document)))
    result.toList
  }

}
