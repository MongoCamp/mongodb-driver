package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.dao.BasePersonSuite
import dev.mongocamp.driver.mongodb.test.TestDatabase
import org.mongodb.scala.bson.BsonDocument

class SelectSqlSuite extends BasePersonSuite {

  test("simple sql") {
    val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age < 30 order by id asc")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 99)
    assertEquals(selectResponse.head.getInteger("age").toInt, 25)
    assertEquals(selectResponse.head.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
  }

  test("simple sql with schema") {
    val queryConverter = MongoSqlQueryHolder("select * from `mongocamp-unit-test`.`people`")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(queryConverter.getCollection, "mongocamp-unit-test:people")
    assertEquals(selectResponse.size, 200)
    assertEquals(selectResponse.head.getString("name"), "Cheryl Hoffman")
    assertEquals(selectResponse.head.getLong("id").toInt, 0)
  }

  test("sql with in query") {
    val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age in (30, 18, 25, 22) order by id asc")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 41)
    assertEquals(selectResponse.head.getInteger("age").toInt, 25)
    assertEquals(selectResponse.head.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
  }

  test("sql with not in query") {
    val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age not in (30, 18, 25, 22) order by id asc")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 159)
    assertEquals(selectResponse.head.getInteger("age").toInt, 40)
    assertEquals(selectResponse.head.getString("guid"), "6ee53e07-2e61-48cd-9bc9-b3505a0438f3")
  }

  test("and sql") {
    val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age < 30 and (age < 30 or age > 30) order by id asc")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 99)
    assertEquals(selectResponse.head.getInteger("age").toInt, 25)
    assertEquals(selectResponse.head.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
  }

  test("and with count") {
    val queryConverter = MongoSqlQueryHolder("select count(*) as anz from people where age < 30 and (age < 30 or age > 30) order by id asc")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.head.getInteger("anz").toInt, 99)
    assertEquals(queryConverter.getCollection, "people")
    assertEquals(queryConverter.getKeysFromSelect, List("anz"))
    assertEquals(queryConverter.hasFunctionCallInSelect, true)
  }

  test("simple select all sql") {
    val queryConverter = MongoSqlQueryHolder("select * from people where age < 30 order by id asc")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 99)
    assertEquals(selectResponse.head.getInteger("age").toInt, 25)
    assertEquals(selectResponse.head.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
  }

  test("simple select between") {
    val queryConverter = MongoSqlQueryHolder("select age, guid as internal from people where balance BETWEEN 1500 AND 2000")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 41)
    assertEquals(selectResponse.head.getInteger("age").toInt, 40)
    assertEquals(selectResponse.head.getString("internal"), "6ee53e07-2e61-48cd-9bc9-b3505a0438f3")
  }

  test("simple select not between") {
    val queryConverter = MongoSqlQueryHolder("select age, guid as internal from people where balance not BETWEEN 1500 AND 2000")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 159)
    assertEquals(selectResponse.head.getInteger("age").toInt, 25)
    assertEquals(selectResponse.head.getString("internal"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
  }

  test("search with with number in string") {
    val queryConverter = MongoSqlQueryHolder(
      "select p1.id, p1.guid, p1.name, p2.age, p2.balance from people as p1 join people as p2 on p1.id = p2.id where p2.age < 30 order by p2.id asc"
    )
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 99)
    val document = selectResponse.head
    assertEquals(document.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
    assertEquals(document.getInteger("age").toInt, 25)
  }

  test("search on join without on expression") {
    val queryConverter = MongoSqlQueryHolder(
      "select p1.id, p1.guid, p1.name, p2.age, p2.balance from people as p1, people as p2 where p1.id = p2.id and p2.age < 30 order by p2.id asc"
    )
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 99)
    val document = selectResponse.head
    assertEquals(document.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
    assertEquals(document("p2").asInstanceOf[BsonDocument].getInt32("age").getValue, 25)
  }

  test("is not null") {
    val queryConverter = MongoSqlQueryHolder("select * from people where age is not null")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 200)
    val document = selectResponse.head
    assertEquals(document.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
    assertEquals(document.getInteger("age").toInt, 25)
  }

  test("is null") {
    val queryConverter = MongoSqlQueryHolder("select * from people where blubber is null")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 200)
    val document = selectResponse.head
    assertEquals(document.getString("guid"), "a17be99a-8913-4bb6-8f14-16d4fa1b3559")
    assertEquals(document.getInteger("age").toInt, 25)
  }

  test("only count") {
    val queryConverter = MongoSqlQueryHolder("select count(*) as tmp, sum(age) from people;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 1)
    val document = selectResponse.head
    assertEquals(document.getInteger("tmp").toInt, 200)
    assertEquals(document.getInteger("sum(age)").toInt, 5961)
  }

  test("group by with count") {
    val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age order by age;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 21)
    val document = selectResponse.head
    assertEquals(document.getInteger("age").toInt, 20)
    assertEquals(document.getInteger("tmp").toInt, 4)
  }

  test("having filter") {
    val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age having count(*) > 10 order by age;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 9)
    val document = selectResponse.head
    assertEquals(document.getInteger("age").toInt, 21)
    assertEquals(document.getInteger("tmp").toInt, 11)
  }

  test("with limit 5") {
    val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age having count(*) > 10 order by age limit 5;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 5)
    val document = selectResponse.head
    assertEquals(document.getInteger("age").toInt, 21)
    assertEquals(document.getInteger("tmp").toInt, 11)
  }

  test("with limit 5 and offset 10") {
    val queryConverter =
      MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age having count(*) > 10 order by age limit 5 offset 5;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 4)
    val document = selectResponse.head
    assertEquals(document.getInteger("age").toInt, 27)
    assertEquals(document.getInteger("tmp").toInt, 12)
    assertEquals(document.getInteger("sum(age)").toInt, 324)
  }

  test("distinct query") {
    val queryConverter = MongoSqlQueryHolder("select distinct favoriteFruit, count(*) from people order by count(*) desc;")
    val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
    assertEquals(selectResponse.size, 3)
    val document = selectResponse.head
    assertEquals(document.getString("favoriteFruit"), "strawberry")
    assertEquals(document.getInteger("count(*)").toInt, 71)
    assertEquals(selectResponse.map(_.getString("favoriteFruit")), List("strawberry", "apple", "banana"))
  }

}
