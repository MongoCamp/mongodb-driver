# Pagination

In many cases you want to have the possibility to paginate over the response of your query result. MongoCamp MongoDB Driver support the pagination over Aggregation and Filter Response. As well there is an comfort methode to have an foreach over the whole Response, but with pagination to have a lower memory footprint.  

## Aggregation Pagination

::: warning
The Pagination over an aggregation pipeline supports only the response of `Document`, also if you use an case class MongoDAO you will got an `Document` back. 
:::
 
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/pagination/PaginationAggregationSpec.scala#aggregation-pagination

## Find Pagination

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/pagination/PaginationFilterSpec.scala#filter-pagination

## Foreach over Pagination result

### With default row count
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/pagination/PaginationIterationSpec.scala#foreach-default-rows

### With specific row count
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/pagination/PaginationIterationSpec.scala#foreach-with-rows
