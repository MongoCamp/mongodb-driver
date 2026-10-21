package dev.mongocamp.driver.mongodb.json

/** Frozen binary compatibility layer, see [[CirceSchema]]. Use [[CirceConverter]]. */
@deprecated("use CirceConverter", "3.3.4")
trait CirceProductSchema {

  def productElementNames(internalProduct: Product): Iterator[String] = CirceConverter.productElementNames(internalProduct)

}
