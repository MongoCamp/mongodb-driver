# Collection Management

`DatabaseProvider` provides first-class helpers for creating MongoDB's specialised collection types, so you never need to drop down to a raw `runCommand` call.

## Capped Collections

A capped collection has a fixed maximum size (and optionally a maximum document count). MongoDB automatically removes the oldest documents when the limit is reached — useful for logs, event queues, and similar append-heavy workloads.

```scala
def createCappedCollection(
  collectionName: String,
  maxSizeBytes: Long,
  maxDocuments: Option[Long] = None,
  databaseName: String = DefaultDatabaseName
): SingleObservable[Unit]
```

### Example

```scala
// 10 MB ring buffer, no document limit
provider.createCappedCollection("audit_log", maxSizeBytes = 10 * 1024 * 1024).result()

// 1 MB ring buffer, at most 1 000 documents
provider.createCappedCollection(
  collectionName = "recent_events",
  maxSizeBytes   = 1024 * 1024,
  maxDocuments   = Some(1000L)
).result()
```

## Time-Series Collections

Time-series collections are optimised for storing measurements or events that arrive in time order. MongoDB stores them in a compressed columnar format for efficient range queries.

::: warning MongoDB 5.0+ Required
Time-series collections require MongoDB 5.0 or later. The operation is skipped gracefully when the server does not support it.
:::

```scala
def createTimeSeriesCollection(
  collectionName: String,
  timeField: String,
  metaField: Option[String] = None,
  granularity: Option[TimeSeriesGranularity] = None,
  databaseName: String = DefaultDatabaseName
): SingleObservable[Unit]
```

| Parameter | Description |
|---|---|
| `timeField` | Field that holds the `Date` timestamp for each document (required). |
| `metaField` | Optional field used to group measurements by source/sensor. |
| `granularity` | Hint to MongoDB for bucket sizing: `SECONDS`, `MINUTES`, or `HOURS`. |

### Example

```scala
import com.mongodb.client.model.TimeSeriesGranularity

provider.createTimeSeriesCollection(
  collectionName = "sensor_readings",
  timeField      = "timestamp",
  metaField      = Some("sensorId"),
  granularity    = Some(TimeSeriesGranularity.SECONDS)
).result()
```

## CollectionInfo helpers

`collectionInfos()` returns a `List[CollectionInfo]`. Each entry now exposes two helpers:

```scala
def isCapped: Boolean    // true when the collection was created as capped
def isTimeSeries: Boolean // true when the collection type is "timeseries"
```

### Example

```scala
val infos = provider.collectionInfos()

infos.filter(_.isCapped).foreach(c => println(s"${c.name} is capped"))
infos.filter(_.isTimeSeries).foreach(c => println(s"${c.name} is time-series"))
```
