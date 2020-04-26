 # Aggregation

## Info

MongoDB support an easy to use [Aggregation Handling](https://docs.mongodb.com/manual/aggregation/).

## Demo

### Setup imports

@@snip [Scala Sources](/src/test/scala/com/sfxcode/nosql/mongo/operation/AggregationSpec.scala) { #agg_imports }

### Define stages

@@snip [Scala Sources](/src/test/scala/com/sfxcode/nosql/mongo/operation/AggregationSpec.scala) { #agg_stages }

### Execute Aggregation

@@@ note { title=Important }

In most cases we have to use the RAW attribute, because the aggregation result not follows the case class pattern. RAW returns always Documents instead of case classes.

@@@

@@snip [Scala Sources](/src/test/scala/com/sfxcode/nosql/mongo/operation/AggregationSpec.scala) { #agg_execute }

### Convert Result

For easy result handling, using the implicit Document to Map conversion can be useful.

@@snip [Scala Sources](/src/test/scala/com/sfxcode/nosql/mongo/operation/AggregationSpec.scala) { #agg_convert }