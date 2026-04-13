# Change Streams

Change streams let your application react to inserts, updates, replaces, and deletes in real time — without polling. They are available at both the collection level (`MongoDAO`) and the database level (`DatabaseProvider`).

::: warning Replica Set Required
Change streams require a MongoDB replica set or MongoDB Atlas. On a standalone server they are not supported.
:::

## Collection-level: MongoDAO.addChangeObserver

### Basic observer

Subscribe to all change events on a collection with a `ChangeObserver[A]`:

```scala
import dev.mongocamp.driver.mongodb.database.ChangeObserver

val observer = ChangeObserver[Person](event => println(s"Change: ${event.getOperationType}"))
PersonDAO.addChangeObserver(observer)
```

### With full document snapshots

By default MongoDB only returns changed fields for updates. Pass `FullDocument.UPDATE_LOOKUP` to always receive the complete document:

```scala
import org.mongodb.scala.model.changestream.FullDocument

PersonDAO.addChangeObserver(observer, FullDocument.UPDATE_LOOKUP)
```

### With pipeline filter

Narrow the stream to specific event types or field values using an aggregation pipeline:

```scala
import org.mongodb.scala.model.Aggregates
import org.mongodb.scala.model.Filters.equal

val pipeline = Seq(Aggregates.`match`(equal("operationType", "insert")))
PersonDAO.addChangeObserver(observer, FullDocument.DEFAULT, pipeline)
```

### With resumeAfter token

Resume an interrupted watch from a stored resume token (e.g. after a restart):

```scala
import org.bson.BsonDocument

val token: BsonDocument = ... // previously stored from event.getResumeToken
PersonDAO.addChangeObserver(observer, FullDocument.UPDATE_LOOKUP, Seq.empty, Some(token))
```

### API reference

```scala
def addChangeObserver(observer: ChangeObserver[A]): ChangeObserver[A]

def addChangeObserver(observer: ChangeObserver[A], fullDocument: FullDocument): ChangeObserver[A]

def addChangeObserver(observer: ChangeObserver[A], fullDocument: FullDocument, pipeline: Seq[Bson]): ChangeObserver[A]

def addChangeObserver(
  observer: ChangeObserver[A],
  fullDocument: FullDocument,
  pipeline: Seq[Bson],
  resumeAfter: Option[BsonDocument]
): ChangeObserver[A]
```

## Database-level: DatabaseProvider.addChangeObserver

Watch all collections in a database at once. The observer type is `ChangeObserver[Document]`.

```scala
// Watch all changes in the default database
provider.addChangeObserver(dbObserver)

// With full document snapshots
provider.addChangeObserver(dbObserver, FullDocument.UPDATE_LOOKUP)

// With pipeline filter
provider.addChangeObserver(dbObserver, FullDocument.DEFAULT, pipeline)

// With resumeAfter
provider.addChangeObserver(dbObserver, FullDocument.UPDATE_LOOKUP, Seq.empty, Some(token))

// On a specific database
provider.addChangeObserver(dbObserver, FullDocument.DEFAULT, Seq.empty, None, "other_db")
```

## ChangeObserver

`ChangeObserver[A]` is a convenience wrapper around the MongoDB reactive-streams `Observer`. The simplest way to create one is with the single-argument factory that accepts a handler for `ChangeStreamDocument[A]` events:

```scala
val observer = ChangeObserver[Person](event => {
  println(s"Operation: ${event.getOperationType}")
  println(s"Full doc : ${event.getFullDocument}")
  println(s"Resume   : ${event.getResumeToken}")
})
```
