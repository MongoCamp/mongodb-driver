# Search Functions

## Info
[MongoDAO](index.md) Search Functions

## Demo

### Setup imports
```scala
// Filter helper functions
import dev.mongocamp.driver.mongodb.Filter._
// sort helper functions
import dev.mongocamp.driver.mongodb.Sort._
// implicits like Document from Map ...
import dev.mongocamp.driver.mongodb._
```

### Execute Search
```scala
val females = PersonDAO.find(Map("gender" -> "female"), sortByKey("name")).resultList()
```
