# Observable

## Info

@@@ note { title=MongoDB_Scala_Driver_Documentation }

The MongoDB Scala Driver is an asynchronous and non blocking driver. Using the Observable model asynchronous events become simple, composable operations, freed from the complexity of nested callbacks.
@@@

See [MongoDB Scala Driver Documentation](http://mongodb.github.io/mongo-scala-driver/2.4/reference/observables/).


## Dealing with Observables

### As Future

```scala
  
  def findAllRestaurants: Future[Seq[Restaurant]] = 
      RestaurantDAO.find().asFuture()

```

### Result Functions

Convert Observable to result object. Import mongo package is needed.

Functions headResult anf resultList have an optional maxWait in seconds parameter (Default maxWait = 10 seconds).

```scala
  import com.sfxcode.nosql.mongo._

  def restaurantsSize = RestaurantDAO.count().headResult()
  
  def findAllRestaurants = RestaurantDAO.find().resultList(maxWait = 20)

```

### Implicit Result Functions

  Implicit result conversion to a given type. Import mongo package is needed.

```scala 
  import com.sfxcode.nosql.mongo._

  def restaurantsSize: Long = RestaurantDAO.count()
  
  def findAllRestaurants:List[Restaurant] = RestaurantDAO.find()

```
