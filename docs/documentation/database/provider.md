# DatabaseProvider

DatabaseProvider is the central repository for MongoClient, databases, collections and DocumentDAO`s.

DatabaseProvider gives access to

* MongoClient
* MongoDatabase
* MongoCollection

## DocumentDAO

From an DocumentDAO you can perform DocumentDAO initialization and caching. On this DAO you can perform CRUD operations.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/database/DatabaseProviderSuite.scala#document-dao

## Transactions

`withTransaction` executes a block inside a client session with automatic commit and rollback:

```scala
provider.withTransaction { session =>
  PersonDAO.insertOne(alice, session).result()
  PersonDAO.updateOne(filter, update, session).result()
}
```

See [Transactions](transactions.md) for full API details.

## Collection Creation

### Capped Collections

```scala
provider.createCappedCollection("audit_log", maxSizeBytes = 10 * 1024 * 1024).result()
```

### Time-Series Collections

```scala
provider.createTimeSeriesCollection(
  collectionName = "sensor_readings",
  timeField      = "timestamp"
).result()
```

See [Collection Management](collections.md) for full API details.

## ~~Registries~~

::: danger
Registries are no longer supported for automatic case class conversion. For scala 3 support we changed from mongodb driver conversion to circe conversion.
:::
