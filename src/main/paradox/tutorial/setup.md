# Tutorial Part 1 - Setup


## MongoDB Demo Database

### Setup

Use Import from MongoDB Website:

[Restaurant Data Import](https://docs.mongodb.com/getting-started/shell/import-data/)

### Sample Document

A sample document in the restaurants collection:

```json
{
  "address": {
     "building": "1007",
     "coord": [ -73.856077, 40.848447 ],
     "street": "Morris Park Ave",
     "zipcode": "10462"
  },
  "borough": "Bronx",
  "cuisine": "Bakery",
  "grades": [
     { "date": { "$date": 1393804800000 }, "grade": "A", "score": 2 },
     { "date": { "$date": 1378857600000 }, "grade": "A", "score": 6 },
     { "date": { "$date": 1358985600000 }, "grade": "A", "score": 10 },
     { "date": { "$date": 1322006400000 }, "grade": "A", "score": 9 },
     { "date": { "$date": 1299715200000 }, "grade": "B", "score": 14 }
  ],
  "name": "Morris Park Bake Shop",
  "restaurant_id": "30075445"
}

```

### Scala case classes for Restaurant document

@@snip [RestaurantDatabase](../../../test/scala/com/sfxcode/nosql/mongo/demo/restaurant/RestaurantDemoDatabase.scala) { #case_classes }