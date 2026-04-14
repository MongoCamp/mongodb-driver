# Find-and-Modify Operations

Atomic read-modify-write operations let you update, delete, or replace a document and receive the result **in a single round-trip**, eliminating any race condition between a separate find and write.

All methods are available on every `MongoDAO[A]`.

## findOneAndUpdate

Atomically applies an update to the first matched document and returns it.

```scala
def findOneAndUpdate(filter: Bson, update: Bson): SingleObservable[A]

def findOneAndUpdate(filter: Bson, update: Bson, options: FindOneAndUpdateOptions): SingleObservable[A]
```

By default MongoDB returns the document **before** the update. Pass `FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)` to get the updated version.

```scala
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.model.FindOneAndUpdateOptions
import org.mongodb.scala.model.ReturnDocument

// Document BEFORE the update
val before = PersonDAO.findOneAndUpdate(
  equal("guid", "abc"),
  set("favoriteFruit", "mango")
).resultOption()

// Document AFTER the update
val after = PersonDAO.findOneAndUpdate(
  equal("guid", "abc"),
  set("favoriteFruit", "mango"),
  FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
).resultOption()
```

`resultOption()` returns `None` when no document matched the filter.

## findOneAndDelete

Atomically removes the first matched document and returns it.

```scala
def findOneAndDelete(filter: Bson): SingleObservable[A]

def findOneAndDelete(filter: Bson, options: FindOneAndDeleteOptions): SingleObservable[A]
```

```scala
val deleted = PersonDAO.findOneAndDelete(equal("guid", "abc")).resultOption()
// deleted == None if no document matched
```

## findOneAndReplace

Atomically replaces the first matched document and returns the old or new version.

```scala
def findOneAndReplace(filter: Bson, replacement: A): SingleObservable[A]

def findOneAndReplace(filter: Bson, replacement: A, options: FindOneAndReplaceOptions): SingleObservable[A]
```

```scala
import org.mongodb.scala.model.FindOneAndReplaceOptions
import org.mongodb.scala.model.ReturnDocument

val updatedPerson = originalPerson.copy(favoriteFruit = "kiwi")

// Returns document BEFORE replace (default)
val before = PersonDAO.findOneAndReplace(
  equal("guid", originalPerson.guid),
  updatedPerson
).resultOption()

// Returns document AFTER replace
val after = PersonDAO.findOneAndReplace(
  equal("guid", originalPerson.guid),
  updatedPerson,
  FindOneAndReplaceOptions().returnDocument(ReturnDocument.AFTER)
).resultOption()
```

## upsertOne

Replaces the first document that matches `filter` with `value`, or inserts `value` as a new document if nothing matches.

```scala
def upsertOne(filter: Bson, value: A): Observable[UpdateResult]
```

```scala
val result = PersonDAO.upsertOne(equal("guid", "new-person"), newPerson).result()

if (result.getUpsertedId != null) println("Document was inserted")
else println("Existing document was replaced")
```
