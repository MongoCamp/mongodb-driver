# JDBC driver

The JDBC driver is a way to use the SQL queries in your application and run them like a 'normal' SQL database. The driver is based on the [MongoSqlQueryHolder](queryholder.md) to convert the SQL query to a Mongo query and execute it on the MongoDB database.

## Usage

### Register Driver
In some environments you have to register the driver manually. This is the case for example in the tests. 

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/jdbc/BaseJdbcSuite.scala#register-driver

After the driver is registered you can use the driver like a normal [JDBC driver](https://www.baeldung.com/java-jdbc).

:::tip
The most default sql statements are supported, but because the difference between MongoDb and SQL the driver can't support SQL statements with subselects. 
:::