# Database

Database Connection can be done using MongConfig.

DatabaseProvider Instance contains setup for registries and databases.

Database provider will ues database by name from MongoConfig by default.
Multiple databases access is supported on the same client is supported. For different MongoDBs you have to use different providers.

## DatabaseProvider
DatabaseProvider is the central repository for MongoClient, registries, databases and collections.

Every @ref:[MongoDAO](dao/index.md) / @ref:[GridFSDAO](gridfs/index.md) Instance needs this class.

@@snip [Mongo Conf](/docs/src/main/scala/DatabaseProviderDoc.scala) { #provider }


## Create MongoConfig with application.conf

### With default path

Default path in application.conf: mongo

@@snip [Mongo Conf](/docs/src/main/resources/docs.conf) { #sample_config }

Scala Code Snippet

@@snip [Mongo Conf](/docs/src/main/scala/DatabaseProviderDoc.scala) { #sample_config_from_path }

### With custom path

@@snip [Mongo Conf](/docs/src/main/resources/docs.conf) { #sample_config_custom }

Scala Code Snippet

@@snip [Mongo Conf](/docs/src/main/scala/DatabaseProviderDoc.scala) { #sample_config_from_custom_path }


## Create MongoConfig with custom ClientSettings

```scala

val config = MongoConfig("my_database", customClientSettings = Some(myClientSettings))
```


## Registries

@@@ note { title=ScalaDriverDocs }

Additional Info for [Registries](https://mongodb.github.io/mongo-java-driver/4.0/driver-scala/getting-started/quick-start-case-class/#configuring-case-classes)

@@@

### Create Case Classes

@@snip [Mongo Conf](/docs/src/main/scala/DatabaseProviderDoc.scala) { #provider_with_registry_classes }

### Create Registry

@@snip [Mongo Conf](/docs/src/main/scala/DatabaseProviderDoc.scala) { #provider_with_registry }


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





