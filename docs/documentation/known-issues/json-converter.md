# Known Issues - Json Converter

## Scala 2
Under scala 2 the json converter have some issues with the json parser and the Any Class in `toJson` Method and needs an explicit cast to `Any`. Circe does not support this format by default.

For scala 3 you can ignore this issue.

<<< @/../src/test/scala/dev/mongocamp/driver/mongodb/json/JsonConversionSuite.scala#known-issue-any-format{scala}

## Values without Json format
`encodeAnyToJson` (used by `AnyCodec` and for `Map[String, Any]` and `Document` values) converts

* `Enumeration` values and Scala 3 `enum` values without parameters to their name, e.g. `"ACTIVE"`
* values of other unknown types to their `toString` value

`decodeFromJson` has no type information, so an `Enumeration` value is decoded as `String`.

## Implicit AnyCodec
`CirceCodecs` has an implicit `Codec[Any]` (`AnyCodec`). Under Scala 3 it can be chosen instead of the encoder of a field type, e.g. for a `Status.Value` field in a sttp request body. To use only your own encoders, override it with a non implicit value in your schema:

```scala
override lazy val AnyCodec: Codec[Any] = Codec.from(Decoder.decodeJson.map(decodeFromJson), Encoder.instance[Any](encodeAnyToJson(_)))
```
