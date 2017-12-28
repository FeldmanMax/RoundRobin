package services

import models.Connection
import org.scalatest.FunSuite
import utils.ConfigurationServiceCreator

class ConfigurationServiceSuite extends FunSuite with ConfigurationServiceCreator{
  test("load connection_with_4_simple_endpoints") {
    val service: ConfigurationService = configServiceWithFileConfiguration()
    val loadedConnection: Either[String, Connection] = for {
      connection <- service.loadConnection("connection_with_4_simple_endpoints").right
    } yield connection

    loadedConnection match {
      case Left(left) => assert(false, left)
      case Right(conn) => assert(conn.endpointsList.lengthCompare(4) == 0)
    }
  }
}
