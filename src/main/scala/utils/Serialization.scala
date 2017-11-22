package utils

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

object Serialization {
	object JsonSerialization {
		private val mapper = new ObjectMapper() with ScalaObjectMapper

		mapper.registerModule(DefaultScalaModule)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

		def serialize(value: Map[Symbol, Any]): String = serialize(value.map{ case (k, v) => k.name -> v})
		def serialize(value: Any): String = {
			val result: String = mapper.writeValueAsString(value)
			result
		}

		def deserialize[T: Manifest](value: String): T = mapper.readValue[T](value)
	}
}


