# DatabaseProvider

DatabaseProvider is the central repository for MongoClient, registries, databases and collections.

DatabaseProvider gives access to

* MongoClient
* MongoDatabase
* MongoCollection

## Registries

::: tip ScalaDriverDocs
Additional Info for [Registries](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/getting-started/quick-start-case-class/#configuring-case-classes)
:::

### Create Case Classes
```scala
case class Student(_id: Long, name: String, scores: List[Score])

case class Score(score: Double, `type`: String)

case class Grade(_id: ObjectId, student_id: Long, class_id: Long, scores: List[Score])
```

### Create Registry
```scala
val registry: CodecRegistry = fromProviders(classOf[Student], classOf[Score], classOf[Grade])

val providerWithRegistry: DatabaseProvider = DatabaseProvider(MongoConfig.fromPath(), registry)
```
## Multiple databases access





