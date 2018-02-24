package utils

import models.internal.{Connection, ConnectionGeneralInfo}
import org.scalatest.FunSuite
import serialization.Serialization

class JsonSerializerSuite extends FunSuite with ConnectionCreator {
  test("Serialize/Deserialize") {
    val connection: Connection = getConnection(ConnectionGeneralInfo("test"), Map("first" -> 100))
    import serialization.ConnectionSerializer._
    Serialization.encode[Connection](connection) match {
      case Left(left) => fail(left)
      case Right(json) => Serialization.decode[Connection](json.toString()) match {
        case Left(left) => fail(left)
        case Right(instance) => assert(connection == instance)
      }
    }
  }
}
