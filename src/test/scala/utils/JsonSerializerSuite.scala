package utils

import models.{Connection, ConnectionGeneralInfo}
import org.scalatest.FunSuite
import utils.Serialization.JsonSerialization

class JsonSerializerSuite extends FunSuite with ConnectionCreator {
  test("Serialize/Deserialize") {
    val connection: Connection = getConnection(ConnectionGeneralInfo("test"), Map("first" -> 100))
    val deserialized = JsonSerialization.deserialize[Connection](JsonSerialization.serialize(connection))
    assert(connection == deserialized)
  }
}
