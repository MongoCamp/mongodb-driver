package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.MongoDAO
import org.apache.lucene.index.Term
import org.apache.lucene.search.Query
import org.apache.lucene.search.WildcardQuery
import org.mongodb.scala.Document

class LuceneEscapeSearchSuite extends munit.FunSuite {

  private object EscapeSearchDAO extends MongoDAO[Document](TestDatabase.provider, "lucene-escape-search-cases")

  // #region lucene-escape-search-cases-data
  private val documents = List(
    Document("label" -> "Mary-Jane Smith", "name"  -> "Mary-Jane Smith"),
    Document("label" -> "Mary-Jane", "name"        -> "Mary-Jane"),
    Document("label" -> "Peter", "name"            -> "Peter"),
    Document("label" -> "100*", "name"             -> "100*"),
    Document("label" -> "1000", "name"             -> "1000"),
    Document("label" -> "a?b", "name"              -> "a?b"),
    Document("label" -> "axb", "name"              -> "axb"),
    Document("label" -> "a.b", "name"              -> "a.b"),
    Document("label" -> "a+b@example.com", "mail"  -> "a+b@example.com"),
    Document("label" -> "aab@example.com", "mail"  -> "aab@example.com"),
    Document("label" -> "x@my-company.org", "mail" -> "x@my-company.org"),
    Document("label" -> "x@my-companyXorg", "mail" -> "x@my-companyXorg"),
    Document("label" -> "+15551234", "phone"       -> "+15551234"),
    Document("label" -> "0015551234", "phone"      -> "0015551234"),
    Document("label" -> "C\\temp", "path"          -> "C\\temp"),
    Document("label" -> "Ctemp", "path"            -> "Ctemp"),
    Document("label" -> "D\\temp\\", "path"        -> "D\\temp\\")
  )
  // #endregion lucene-escape-search-cases-data

  case class SearchCase(query: String, expected: List[String], description: String)

  // #region lucene-escape-search-cases
  private val allLabels = documents.map(_.getStringValue("label"))

  private def allExcept(labels: List[String]): List[String] = allLabels.diff(labels)

  private val searchCases = List(
    SearchCase("""name:*Mary\-Jane*""", List("Mary-Jane Smith", "Mary-Jane"), "escaped - in a contains search"),
    SearchCase("""mail:*a\+b@example.com*""", List("a+b@example.com"), "escaped + is a literal +"),
    SearchCase("""mail:*@my\-company.org""", List("x@my-company.org"), "escaped - in an ends with search, . is literal"),
    SearchCase("""phone:*\+1555*""", List("+15551234"), "escaped + at the beginning"),
    SearchCase("""name:*100\**""", List("100*"), "escaped * is a literal *"),
    SearchCase("""name:*a\?b*""", List("a?b"), "escaped ? is a literal ?"),
    SearchCase("""name:*a?b*""", List("a?b", "axb", "a.b"), "unescaped ? matches one character"),
    SearchCase("""name:*a.b*""", List("a.b"), "unescaped . is a literal ."),
    SearchCase("""path:*C\\temp*""", List("C\\temp"), "escaped backslash is a literal backslash"),
    SearchCase("""NOT name:*Mary\-Jane*""", allExcept(List("Mary-Jane Smith", "Mary-Jane")), "negated wildcard with escaped -"),
    SearchCase("""-name:*Mary\-Jane*""", allExcept(List("Mary-Jane Smith", "Mary-Jane")), "negated wildcard with escaped - and minus"),
    SearchCase("""name:*mary\-jane*""", List("Mary-Jane Smith", "Mary-Jane"), "wildcard with escaped - is case-insensitive"),
    SearchCase("""name:*MARY\-JANE*""", List("Mary-Jane Smith", "Mary-Jane"), "upper case wildcard with escaped -"),
    SearchCase("""name:(*Mary\-Jane* OR Peter)""", List("Mary-Jane Smith", "Mary-Jane", "Peter"), "escaped - in a group"),
    SearchCase("""name:Mary\-Jane* AND NOT name:*Smith""", List("Mary-Jane"), "escaped - combined with a negated wildcard"),
    SearchCase("""name:Mary\-Ja*""", List("Mary-Jane Smith", "Mary-Jane"), "escaped - in a starts with search"),
    SearchCase("""name:100\**""", List("100*"), "escaped * in a starts with search"),
    SearchCase("""path:C\\te*""", List("C\\temp"), "escaped backslash in a starts with search"),
    SearchCase("""-name:Mary\-Ja*""", allExcept(List("Mary-Jane Smith", "Mary-Jane")), "negated starts with search with escaped -"),
    SearchCase("""name:Mary\-Jane""", List("Mary-Jane"), "escaped - in an exact value"),
    SearchCase("""name:100\*""", List("100*"), "escaped * in a value without wildcard is no wildcard"),
    SearchCase("""path:C\\temp""", List("C\\temp"), "escaped backslash in an exact value"),
    SearchCase("""name:"100*"""", List("100*", "1000"), "* in a quoted value is a wildcard"),
    SearchCase("""name:"100\**"""", List("100*", "1000"), "\\* in a quoted value is a wildcard too"),
    SearchCase("""name:"*a?b*"""", List("a?b"), "? in a quoted value is a literal ?"),
    SearchCase("""path:"*C\\temp*"""", List("C\\temp"), "escaped backslash in a quoted wildcard value")
  )
  // #endregion lucene-escape-search-cases

  override def beforeAll(): Unit = {
    EscapeSearchDAO.drop().result()
    EscapeSearchDAO.insertMany(documents).result()
  }

  override def afterAll(): Unit = {
    EscapeSearchDAO.drop().result()
  }

  private def search(query: Query): List[String] = EscapeSearchDAO.find(query).resultList().map(_.getStringValue("label")).sorted

  searchCases.foreach(
    searchCase =>
      test(s"${searchCase.description}: ${searchCase.query}") {
        assertEquals(search(LuceneQueryConverter.parse(searchCase.query, "label")), searchCase.expected.sorted)
      }
  )

  test("backslash at the end of a wildcard value is a literal backslash") {
    assertEquals(search(new WildcardQuery(new Term("path", "*temp\\"))), List("D\\temp\\"))
  }

}
