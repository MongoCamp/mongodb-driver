# DatabaseProvider

DatabaseProvider is the central repository for MongoClient, databases, collections.
It is referenced by the @ref:[MongoDAO](dao/index.md) Patttern.

## MongoConfig

MongoConfig holds all the neded Data for creating a MongoClient.
It is used for DatabaseProvider creation.

| Key                  | Description               | Default value      |
|:---------------------|:--------------------------|:-------------------|
| database             | default database to use   |                    |
| host                 |                           | 127.0.0.1          |
| port                 |                           | 27017              |
| applicationName      |                           | simple-mongo-app   |
| userName             | used for Authentification |                    |
| password             | used for Authentification |                    |
| poolOptions          |                           | MongoPoolOptions() |
| customClientSettings |                           | None               |


## MongoPoolOptions

| Key                            | Default value |
|:-------------------------------|:--------------|
| maxConnectionIdleTime          | 60            |
| maxSize                        | 50            |
| minSize                        | 0             |
| maxWaitQueueSize               | 500           |
| DefaultMaintenanceInitialDelay | 0             |


## Create MongoConfig with properties

Example:

```scala

val config = MongoConfig("my_database", host = "localhost",
        applicationName = "Awesome Application Name")

```

## Create MongoConfig with config

Create application.conf:

```hocon

config.test.auth.mongo {
  database = "another_database"
  host = "localhost"
  port = 270007
  applicationName = "simple-mongo-config-test-with-auth"
  userName = "admin_user"
  password = "1234"
  pool {
    minSize = 5
    maxSize = 100
  }

```

```scala
val config = MongoConfig.fromPath("config.test.auth.mongo")
```

## Create MongoConfig with custom ClientSettings

```scala

val config = MongoConfig("my_database", customClientSettings = Some(myClientSettings))
```


## Registries







