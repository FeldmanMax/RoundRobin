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
        assert(conn.endpoints.size == 2)
    }
  }

  test("load connection_2") {
    val service: ConfigurationService = configServiceWithFileConfiguration()
    val loadedConnection: Either[String, Connection] = for {
      connection <- service.loadConnection("connection_2").right
    } yield connection

    loadedConnection match {
      case Left(left) => assert(false, left)
      case Right(conn) =>
        assert(conn.info.isUsingConnections)
    }
  }

  test("load connection_3") {
    val service: ConfigurationService = configServiceWithFileConfiguration()
    val loadedConnection: Either[String, List[Connection]] = for {
      connection <- service.loadConnections("connection_3").right
    } yield connection

    loadedConnection match {
      case Left(left) => assert(false, left)
      case Right(conn) => assert(conn.size == 2)
    }
  }
}
