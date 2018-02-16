package services

import models.internal.Connection
import org.scalatest.FunSuite
import roundrobin.api.ConnectionAPI
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

  test("load http_response_200") {
    ConnectionAPI.next("http_response_200") match {
      case Left(left) => fail(left)
      case Right(res) => assert(res.connectionName == "http_response_200")
    }
  }
}
