package dev.mongocamp.driver.mongodb
import io.circe.generic.AutoDerivation

package object json extends CirceCodecs with AutoDerivation {}
