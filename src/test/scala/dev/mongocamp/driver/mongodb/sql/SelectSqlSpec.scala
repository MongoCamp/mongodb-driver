package dev.mongocamp.driver.mongodb.sql

import dev.mongocamp.driver.mongodb.GenericObservable
import dev.mongocamp.driver.mongodb.dao.PersonSpecification
import dev.mongocamp.driver.mongodb.test.TestDatabase

class SelectSqlSpec extends PersonSpecification {

  "MongoSqlQueryHolder Select" should {
    "simple sql" in {
      val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age < 30 order by id asc")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 99
      selectResponse.head.getInteger("age") mustEqual 25
      selectResponse.head.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
    }

    "simple sql with schema" in {
      val queryConverter = MongoSqlQueryHolder("select * from `mongocamp-unit-test`.`friend`")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 1327
      selectResponse.head.getString("name") mustEqual "Castaneda Mccullough"
      selectResponse.head.getLong("id") mustEqual 33
    }

    "sql with in query" in {
      val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age in (30, 18, 25, 22) order by id asc")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 41
      selectResponse.head.getInteger("age") mustEqual 25
      selectResponse.head.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
    }

    "sql with not in query" in {
      val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age not in (30, 18, 25, 22) order by id asc")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 159
      selectResponse.head.getInteger("age") mustEqual 40
      selectResponse.head.getString("guid") mustEqual "6ee53e07-2e61-48cd-9bc9-b3505a0438f3"
    }

    "and sql" in {
      val queryConverter = MongoSqlQueryHolder("select id, guid, name, age, balance from people where age < 30 and (age < 30 or age > 30) order by id asc")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 99
      selectResponse.head.getInteger("age") mustEqual 25
      selectResponse.head.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
    }

    "simple select all sql" in {
      val queryConverter = MongoSqlQueryHolder("select * from people where age < 30 order by id asc")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 99
      selectResponse.head.getInteger("age") mustEqual 25
      selectResponse.head.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
    }

    "simple select between" in {
      val queryConverter = MongoSqlQueryHolder("select age, guid as internal from people where balance BETWEEN 1500 AND 2000")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 41
      selectResponse.head.getInteger("age") mustEqual 40
      selectResponse.head.getString("internal") mustEqual "6ee53e07-2e61-48cd-9bc9-b3505a0438f3"
    }

    "simple select not between" in {
      val queryConverter = MongoSqlQueryHolder("select age, guid as internal from people where balance not BETWEEN 1500 AND 2000")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 159
      selectResponse.head.getInteger("age") mustEqual 25
      selectResponse.head.getString("internal") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
    }

    "search with with number in string" in {
      val queryConverter = MongoSqlQueryHolder(
        "select p1.id, p1.guid, p1.name, p2.age, p2.balance from people as p1 join people as p2 on p1.id = p2.id where p2.age < 30 order by p2.id asc"
      )
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 99
      val document = selectResponse.head
      document.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
      document.getInteger("age") mustEqual 25
    }

    "is not null" in {
      val queryConverter = MongoSqlQueryHolder("select * from people where age is not null")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 200
      val document = selectResponse.head
      document.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
      document.getInteger("age") mustEqual 25
    }

    "is null" in {
      val queryConverter = MongoSqlQueryHolder("select * from people where blubber is null")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 200
      val document = selectResponse.head
      document.getString("guid") mustEqual "a17be99a-8913-4bb6-8f14-16d4fa1b3559"
      document.getInteger("age") mustEqual 25
    }

    "group by with count" in {
      val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age order by age;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 21
      val document = selectResponse.head
      document.getInteger("age") mustEqual 20
      document.getInteger("tmp") mustEqual 4
    }

    "having filter" in {
      val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age having count(*) > 10 order by age;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 9
      val document = selectResponse.head
      document.getInteger("age") mustEqual 21
      document.getInteger("tmp") mustEqual 11
    }

    "with limit 5" in {
      val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age having count(*) > 10 order by age limit 5;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 5
      val document = selectResponse.head
      document.getInteger("age") mustEqual 21
      document.getInteger("tmp") mustEqual 11
    }

    "with limit 5 and offset 10" in {
      val queryConverter = MongoSqlQueryHolder("select age, count(*) as tmp, sum(age) from people group by age having count(*) > 10 order by age limit 5 offset 5;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 4
      val document = selectResponse.head
      document.getInteger("age") mustEqual 27
      document.getInteger("tmp") mustEqual 12
      document.getInteger("sum(age)") mustEqual 324
    }

    "destinct" in {
      val queryConverter = MongoSqlQueryHolder("select distinct favoriteFruit, count(*) from people order by count(*) desc;")
      val selectResponse = queryConverter.run(TestDatabase.provider).resultList()
      selectResponse.size mustEqual 3
      val document = selectResponse.head
      document.getString("favoriteFruit") mustEqual "strawberry"
      document.getInteger("count(*)") mustEqual 71
      selectResponse.map(_.getString("favoriteFruit")) mustEqual List("strawberry", "apple", "banana")
    }

  }
}
