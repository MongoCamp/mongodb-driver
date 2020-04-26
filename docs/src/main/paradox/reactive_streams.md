# Reactive Streams

* The MongoDB Scala driver is built upon [Reactive Streams](https://www.reactive-streams.org/)
* simple-mongo wraps around the scala driver => Full support of Reactive Streams
* For Blocking Results (implicit) conversion to Result Objects is provided
* Conversion of Observable to Future is available


@@@ note { title=ScalaDriverDocs }

Additional Info for [Reactive Streams JVM](https://github.com/reactive-streams/reactive-streams-jvm/)

Additional Info for [Mongo Scala Reactive Streams](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/getting-started/quick-start-primer/)

@@@

## Blocking Results

Conversion is provided in the DAO instances with four Helper function for Observable[C]:

| Funcion Name | Function Result | Sample                                         | Sample Result  |
|:-------------|:----------------|:-----------------------------------------------|:---------------|
| result       | C               | BookDAO.count().result()                       | Long           |
| results      | Seq[C]          | PersonDAO.findAggregated(aggregator).results() | Seq[Person]    |
| resultList   | List[C]         | UserDAO.find("name", "User").resultList()      | List[User]     |
| resultOption | Option[C]       | PersonDAO.find(Map("id" -> 42)).resultOption() | Option[Person] |

### Implicit Result Conversion (Blocking)

To use implicit result conversion, you have to import the simple mongo base package object.

```scala
import com.sfxcode.nosql.mongo._
```

After that, implicit conversion and other useful implicits (e.g. Map -> Bson) are available.

@@snip [Mongo Conf](/docs/src/main/scala/ReactiveStreamsDoc.scala) { #implicit_result_conversion }

## Future Results

DAO Instances support (implicit) conversion to Future,

@@snip [Mongo Conf](/docs/src/main/scala/ReactiveStreamsDoc.scala) { #as_future }
