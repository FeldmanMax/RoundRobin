package services

import models.Connection
import org.scalatest.FunSuite
import utils.ConfigurationServiceCreator

class ConfigurationServiceSuite extends FunSuite with ConfigurationServiceCreator{
  test("load connection_1") {
    val service: ConfigurationService = configServiceWithFileConfiguration()
    val loadedConnection: Either[String, Connection] = for {
      connection <- service.loadConnection("connection_1").right
    } yield connection

    loadedConnection match {
      case Left(left) => assert(false, left)
      case Right(conn) =>
        val end = conn.endpoints
        assert(conn.endpoints.size == 2)
    }
  }
}
