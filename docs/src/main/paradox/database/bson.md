# Bson

## BSON converter

BSON converter is used for bidirectional converting of BSON data.

* Supports most Scala / Java types.
* Case class support
* Java Bean support
* pluggable

## Usage

### toBson

```scala

  val result = BsonConverter.toBson(3) // result = BsonInt32(3)

```


### fromBson

```scala

  val result = BsonConverter.fromBson(BsonInt32(3)) // result = 3

```

### toDocument

```scala

    case class Base(int: Int, Long: Long, float: Float, double: Double, 
      string: String, date: Date = new Date())

    object Base {
      def apply(): Base = new Base(1, 2, 3, 4, "test")
    }

   val document = Converter.toDocument(Base())

    // Document((float,BsonDouble{value=3.0}), (string,BsonString{value='test'}), 
    // (double,BsonDouble{value=4.0}), (Long,BsonInt64{value=2}), (date,
    // BsonDateTime{value=1531166757627}), (int,BsonInt32{value=1}))


```

## Plugins

### Example

@@snip [JodaConverterPlugin](/src/test/scala/dev/mongocamp/driver/mongodb/bson/JodaConverterPlugin.scala)

