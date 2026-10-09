package dev.mongocamp.driver.mongodb.lucene

import dev.mongocamp.driver.mongodb._
import dev.mongocamp.driver.mongodb.test.TestDatabase
import dev.mongocamp.driver.mongodb.MongoDAO
import java.time.Instant
import java.util.Date
import org.mongodb.scala.Document

/** Documents the behavior of lucene queries in MongoDB. Every search case is a test, the search cases are part of the documentation. */
class LuceneSearchCasesSuite extends munit.FunSuite {

  private object SearchCasesDAO extends MongoDAO[Document](TestDatabase.provider, "lucene-search-cases")

  private def date(value: String): Date = Date.from(Instant.parse(value))

  // #region lucene-search-cases-data
  private val documents = List(
    Document("name" -> "John W. Dowe", "email"   -> "john.w.dowe@example.com", "registered" -> date("2014-04-19T22:44:27Z")),
    Document("name" -> "JohnDowe", "email"       -> "john+dowe@example.com"),
    Document("name" -> "John Dowe", "email"      -> "john.dowe@example.com", "registered"   -> date("2014-04-20T10:00:00Z")),
    Document("name" -> "Big John W. Dowe"),
    Document("name" -> "John W. Dowe Jr."),
    Document("name" -> "Jane Dowe", "registered" -> date("2014-04-22T23:59:59Z")),
    Document("name" -> "john dowe"),
    Document("name" -> "Hallo Welt"),
    Document("name" -> "Hallo  Welt"),
    Document("name" -> "Hallo \"Welt\""),
    Document("name" -> "Wie geht's?"),
    Document("name" -> "Hallo"),
    Document("name" -> "Nick Welt", "nickname"   -> "Welt"),
    Document("name" -> "O'Brien", "email"        -> "o'brien@example.com"),
    Document("name" -> "Example X", "email"      -> "x@exampleXcom"),
    Document("name" -> "Line Breaks", "text"     -> "first line\nsecond line")
  )
  // #endregion lucene-search-cases-data

  case class SearchCase(query: String, expected: List[String], description: String)

  // the default field of all queries is nickname
  // #region lucene-search-cases
  private val allNames = documents.map(_.getStringValue("name"))

  private def allExcept(names: List[String]): List[String] = allNames.diff(names)

  private val startsWithJohn = List("John W. Dowe", "JohnDowe", "John Dowe", "John W. Dowe Jr.", "john dowe")

  private val searchCases = List(
    // email addresses are one value
    SearchCase("""email:john.w.dowe@example.com""", List("John W. Dowe"), "email address is searched as one value"),
    SearchCase("""email:"john.w.dowe@example.com"""", List("John W. Dowe"), "quoted email address"),
    SearchCase("""email:'john.w.dowe@example.com'""", List("John W. Dowe"), "single quotes are removed"),
    SearchCase("""email:john+dowe@example.com""", List("JohnDowe"), "email address with plus"),
    SearchCase("""email:o'brien@example.com""", List("O'Brien"), "single quote inside the value is kept"),
    SearchCase("""email:*@example.com""", List("John W. Dowe", "JohnDowe", "John Dowe", "O'Brien"), "regex characters like . are escaped"),
    SearchCase("""-email:john.w.dowe@example.com""", allExcept(List("John W. Dowe")), "negated email address"),
    // quoted values are exact values
    SearchCase("""name:"John Dowe"""", List("John Dowe"), "quoted value is searched as exact value"),
    SearchCase("""name:"john dowe"""", List("john dowe"), "exact value is case-sensitive"),
    SearchCase("""name:"John"""", List(), "exact value is no contains search"),
    SearchCase("""name:"Hallo Welt"""", List("Hallo Welt"), "exact value with whitespace"),
    SearchCase("""name:"Hallo  Welt"""", List("Hallo  Welt"), "whitespace of the exact value is kept"),
    SearchCase("""name:"Hallo \"Welt\""""", List("Hallo \"Welt\""), "quotes are escaped with a backslash"),
    SearchCase("""name:"Wie geht's?"""", List("Wie geht's?"), "? is no wildcard in a quoted value"),
    SearchCase("""-name:"John Dowe"""", allExcept(List("John Dowe")), "negated exact value"),
    // unquoted values with whitespace are multiple queries
    SearchCase("""name: Hallo Welt""", List("Hallo", "Nick Welt"), "name is Hallo OR the default field is Welt"),
    SearchCase("""name:John* Dowe""", startsWithJohn, "name starts with John OR the default field is Dowe"),
    // wildcards have to match the whole value and are case-insensitive
    SearchCase("""name:John*""", startsWithJohn, "starts with"),
    SearchCase("""name:*Dowe""", List("John W. Dowe", "JohnDowe", "John Dowe", "Big John W. Dowe", "Jane Dowe", "john dowe"), "ends with"),
    SearchCase("""name:"*W. Dowe*"""", List("John W. Dowe", "Big John W. Dowe", "John W. Dowe Jr."), "contains"),
    SearchCase("""name:"John*Dowe"""", List("John W. Dowe", "JohnDowe", "John Dowe", "john dowe"), "* matches any text, also no text"),
    SearchCase("""name:"John * Dowe"""", List("John W. Dowe"), "* between whitespace needs text between the words"),
    SearchCase(
      """name:"*John*Dowe*"""",
      List("John W. Dowe", "JohnDowe", "John Dowe", "Big John W. Dowe", "John W. Dowe Jr.", "john dowe"),
      "contains John and later Dowe"
    ),
    SearchCase("""name:J?hn*""", startsWithJohn, "? matches one character"),
    SearchCase("""-name:John*""", allExcept(startsWithJohn), "negated wildcard"),
    SearchCase("""text:*second*""", List("Line Breaks"), "contains in a value with line breaks"),
    SearchCase("""text:first*""", List("Line Breaks"), "starts with in a value with line breaks"),
    SearchCase("""text:*line?second*""", List("Line Breaks"), "? matches a line break"),
    SearchCase("""text:"*line second*"""", List(), "a line break is no whitespace"),
    // dates keep the time zone offset, dates without offset use the default time zone of the JVM
    SearchCase("""registered:20140420T004427000\+0200""", List("John W. Dowe"), "date with time zone offset"),
    SearchCase("""registered:"2014-04-20T00:44:27+02:00"""", List("John W. Dowe"), "ISO date with time zone offset"),
    SearchCase("""registered:20140419T224427000Z""", List("John W. Dowe"), "date in UTC"),
    SearchCase("""registered:2014-04-19T22\:44\:27Z""", List("John W. Dowe"), "ISO date in UTC"),
    SearchCase("""registered:[2014-04-20T00:00:00Z TO 2014-04-22T23:59:59Z]""", List("John Dowe", "Jane Dowe"), "date range"),
    SearchCase(
      """registered:[2014-04-20T00\:00\:00+02\:00 TO 2014-04-21T00\:00\:00+02\:00]""",
      List("John W. Dowe", "John Dowe"),
      "date range with time zone offset"
    )
  )
  // #endregion lucene-search-cases

  override def beforeAll(): Unit = {
    SearchCasesDAO.drop().result()
    SearchCasesDAO.insertMany(documents).result()
  }

  override def afterAll(): Unit = {
    SearchCasesDAO.drop().result()
  }

  searchCases.foreach(
    searchCase =>
      test(s"${searchCase.description}: ${searchCase.query}") {
        val query  = LuceneQueryConverter.parse(searchCase.query, "nickname")
        val result = SearchCasesDAO.find(query).resultList().map(_.getStringValue("name"))
        assertEquals(result.sorted, searchCase.expected.sorted)
      }
  )

}
