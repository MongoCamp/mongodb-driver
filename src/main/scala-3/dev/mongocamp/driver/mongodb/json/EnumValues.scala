package dev.mongocamp.driver.mongodb.json

private[json] object EnumValues {

  def isValueWithoutParameters(value: Any): Boolean = {
    value match {
      case e: scala.reflect.Enum => e.productArity == 0
      case _                     => false
    }
  }

}
