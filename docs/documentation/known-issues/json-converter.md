# Known Issues - Json Converter

## Scala 2
Under scala 2 the json converter have some issues with the json parser and the Any Class in `toJson` Method and needs an explicit cast to `Any`. Circe does not support this format by default.

For scala 3 you can ignore this issue.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/json/JsonConversionSuite.scala#known-issue-any-format{scala}
