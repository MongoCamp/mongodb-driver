# MongConfig

Database Configuration can be done using MongConfig.

## Create MongoConfig with application.conf

### With default path

Default path in application.conf: mongo

@@snip [Scala Sources](/docs/src/main/resources/docs.conf) { #sample_config }

Scala Code Snippet

@@snip [Scala Sources](/docs/src/main/scala/DatabaseProviderDoc.scala) { #sample_config_from_path }

### With custom path

@@snip [Scala Sources](/docs/src/main/resources/docs.conf) { #sample_config_custom }

Scala Code Snippet

@@snip [Scala Sources](/docs/src/main/scala/DatabaseProviderDoc.scala) { #sample_config_from_custom_path }


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
| applicationName      |                           | simple-mongo-app   |
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

## Multiple databases access





