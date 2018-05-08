# MongoDAO - Search Functions

## Info

## Demo

### Setup imports

```scala
// Filter helper functions
import com.sfxcode.nosql.mongo.Filter._
// sort helper functions
import com.sfxcode.nosql.mongo.Sort._
// implicits like Document from Map ...
import com.sfxcode.nosql.mongo._
```

### Execute Search

```scala
val females = PersonDAO.find(Map("gender" -> "female"), 
    sortByKey("name")).resultList()


```
