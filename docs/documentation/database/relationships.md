# Relationships

## Info

Normal Relationship handling is the use of [embedded documents](https://docs.mongodb.com/manual/tutorial/model-embedded-one-to-one-relationships-between-documents/).
However, sometimes there is a need for [relationsips beetween collections](https://docs.mongodb.com/manual/tutorial/model-referenced-one-to-many-relationships-between-documents/). There is a Relations Trait to be used in case classes for easy relationship handling.

## Relations trait

The Relations trait extends DAO case classes with relationship functions.

* relatedRecordForOneToOne (OneToOneRelationship and reference value needed)
* relatedRecordsForOneToMany (OneToManyRelationship and reference value needed)

## Demo
Simple Setup.

* User Collection(should have one login and multiple friends)
* Login Collection
* Friend Collection

```scala
import dev.mongocamp.driver.mongodb._

case class User(id: Long, name: String, loginId: String)
case class Login(id: String, email: String, password: String)
case class Friend(id: Long, name: String, userId: Long)

object UserDAO extends MongoDAO[User](database, "user")
object LoginDAO extends MongoDAO[Login](database, "login")
object FriendDAO extends MongoDAO[Friend](database, "friend")
```

For relationship setup we create two Relationships in the UserDAO.

* OneToOne loginRelation  (LoginDAO, key is id in user collection)
* OneToMany friendsRelation (FriendDAO, key is userId in friend collection)

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/relation/RelationDemoDatabase.scala#user_dao


We extend the User case class with the Relations trait and add relation specific functions.

* login (create an Option of Login)
* friends (create a List of Friend)

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/relation/RelationDemoDatabase.scala#user_class


