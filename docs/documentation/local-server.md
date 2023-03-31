# LocalServer

mongocamp supports LocalServer by [mongo-java-server](https://github.com/bwaldvogel/mongo-java-server) (since 2.0.1).

Useful for tests and small offline Apps. Dependency to mongo-java-server is marked as provided, you have to import the dependency by yourself.

```scala
// Base dependency
libraryDependencies += "de.bwaldvogel" % "mongo-java-server" % "1.28.0"

// H2 Backend
libraryDependencies += "de.bwaldvogel" % "mongo-java-server-h2-backend" % "1.28.0"
```

## LocalServer Modes

* InMemory
* H2 InMemory
* H2 file based

## Setup

LocalServer is done by ServerConfig. For InMemory tests no special stup is needed.

```scala
val LocalTestServer = LocalServer()
```

### Setup with application config

<<< @/../src/test/resources/application.conf#local_server{json}





