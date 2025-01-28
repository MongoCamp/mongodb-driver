package dev.mongocamp.driver.mongodb.json

trait CirceProductSchema {

  def productElementNames(internalProduct: Product): Iterator[String] = {
    internalProduct.productElementNames
  }

}
