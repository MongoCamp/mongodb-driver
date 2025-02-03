# MongConfig

Database Configuration can be done using MongConfig.

## Create MongoConfig with application.conf

### With default path
Default path in application.conf: mongo
```json
mongo {
  database = "university"
  host = "localhost"
  port = 270007
  applicationName = "mongo.db.sample"
  userName = "standard_user"
  password = "change_me"
  pool {
    minSize = 5
    maxSize = 100
  }
}
```

Scala Code Snippet
```scala
val config: MongoConfig = MongoConfig.fromPath()
```

### With custom path
```json
mongo.db.test {
  database = "unit_test"
  host = "localhost"
  port = 270007
  applicationName = "mongo.db.unit.test"
  userName = "unit_test"
  password = "change_me"
}
```

Scala Code Snippet
```scala
val customConfig: MongoConfig = MongoConfig.fromPath("mongodb.db.prod")
```


## Create MongoConfig with custom ClientSettings
```scala
val config = MongoConfig("my_database", customClientSettings = Some(myClientSettings))
```

## MongoConfig Options
MongoConfig holds all the neded Data for creating a MongoClient.

It is used for DatabaseProvider creation.

| Key                  | Description               | Default value      |
|:---------------------|:--------------------------|:-------------------|
| database             | default database to use   |                    |
| host                 |                           | 127.0.0.1          |
| port                 |                           | 27017              |
| applicationName      |                           | mongocamp-app   |
| userName             | used for Authentification |                    |
| password             | used for Authentification |                    |
| poolOptions          |                           | MongoPoolOptions() |
| compressors          | List: zlib, snappy, zstd  | List()             |
| customClientSettings |                           | None               |


## MongoConfig Pool Options

| Key                            | Default value |
|:-------------------------------|:--------------|
| maxConnectionIdleTime          | 60            |
| maxSize                        | 50            |
| minSize                        | 0             |
| DefaultMaintenanceInitialDelay | 0             |

