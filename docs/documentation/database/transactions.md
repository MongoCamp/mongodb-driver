# Transactions

MongoDB multi-document ACID transactions let you execute several write operations atomically — either all succeed, or none are applied.

::: warning Replica Set Required
Transactions require a MongoDB replica set or MongoDB Atlas. When running against a standalone server or the embedded test server they are skipped gracefully.
:::

## withTransaction

`DatabaseProvider` provides a `withTransaction` helper that manages the session lifecycle for you:

```scala
def withTransaction[T](block: ClientSession => T): T
```

* Starts a `ClientSession` and calls `startTransaction()`.
* Passes the session into `block`.
* **Commits** the transaction if `block` returns normally.
* **Aborts** the transaction (rolls back) if `block` throws any exception — the exception is re-thrown.
* Always closes the session in a `finally` block.

### Example — commit

```scala
provider.withTransaction { session =>
  PersonDAO.insertOne(alice, session).result()
  PersonDAO.insertOne(bob, session).result()
}
// Both inserted atomically, or neither if an error occurred
```

### Example — rollback

```scala
intercept[RuntimeException] {
  provider.withTransaction { session =>
    PersonDAO.insertOne(alice, session).result()
    throw new RuntimeException("something went wrong")
    // alice is NOT persisted — the transaction was aborted
  }
}
```

## Session-aware CRUD overloads

All write methods on `MongoDAO` accept an optional `ClientSession` as the last parameter so you can enlist individual operations in an existing transaction:

```scala
// Create
def insertOne(value: A, session: ClientSession): Observable[InsertOneResult]
def insertMany(values: Seq[A], session: ClientSession): Observable[InsertManyResult]

// Update
def replaceOne(filter: Bson, value: A, session: ClientSession): Observable[UpdateResult]
def updateOne(filter: Bson, update: Bson, session: ClientSession): Observable[UpdateResult]
def updateMany(filter: Bson, update: Bson, session: ClientSession): Observable[UpdateResult]

// Delete
def deleteOne(filter: Bson, session: ClientSession): Observable[DeleteResult]
def deleteMany(filter: Bson, session: ClientSession): Observable[DeleteResult]
```

## Session-aware find overloads

Read operations can also be scoped to a session to see the transactional writes made so far:

```scala
def find(session: ClientSession, filter: Bson, sort: Bson, projection: Bson, limit: Int, skip: Int): Observable[A]
def find(session: ClientSession, filter: Bson): Observable[A]
def find(session: ClientSession): Observable[A]
```

### Example — read inside a transaction

```scala
provider.withTransaction { session =>
  val docs = PersonDAO.find(session).resultList()
  // includes documents inserted earlier in this same transaction
}
```
