# Aggregation

## Info

MongoDB support an easy to use [Aggregation Handling](https://docs.mongodb.com/manual/aggregation/).

## Demo

### Setup imports
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/operation/AggregationSuite.scala#agg_imports

### Define stages
<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/operation/AggregationSuite.scala#agg_stages

### Execute Aggregation

::: warning Important
 In most cases we have to use the RAW attribute, because the aggregation result not follows the case class pattern. RAW returns always Documents instead of case classes.
:::


<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/operation/AggregationSuite.scala#agg_execute

### Convert Result

For easy result handling, using the implicit Document to Map conversion can be useful.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/operation/AggregationSuite.scala#agg_convert
