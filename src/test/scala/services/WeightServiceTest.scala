package services

import org.scalatest.FunSuite
import roundrobin.models.api.EndpointWeight
import utils.ConnectionServiceCreator

class WeightServiceTest extends FunSuite with ConnectionServiceCreator {
  test("active single endpoint should have weight of 100 points when loads") {
    val connectionService: ConnectionService = getConnectionService()
    connectionService.load("one_active") match {
      case Left(left) => fail(left)
      case Right(connection) =>
        val weightService: WeightService = getConnectionService().weightService
        val connectionWeights: List[EndpointWeight] = weightService.getConnectionWeight(connection)
        assert(connectionWeights.lengthCompare(1) == 0)
        assert(connectionWeights.head.points.lengthCompare(100) == 0)
    }
  }

  test("active two endpoints should have weight of 100 points when loads") {
    val connectionService: ConnectionService = getConnectionService()
    connectionService.load("two_active") match {
      case Left(left) => fail(left)
      case Right(connection) =>
        val weightService: WeightService = getConnectionService().weightService
        val connectionWeights: List[EndpointWeight] = weightService.getConnectionWeight(connection)
        assert(connectionWeights.lengthCompare(2) == 0)
        assert(connectionWeights.head.points.lengthCompare(100) == 0 && connectionWeights.tail.head.points.lengthCompare(100) == 0)
    }
  }

  test("inactive single endpoint should have weight of 0 points when loads") {
    val connectionService: ConnectionService = getConnectionService()
    connectionService.load("one_inactive") match {
      case Left(left) => fail(left)
      case Right(connection) =>
        val weightService: WeightService = getConnectionService().weightService
        val connectionWeights: List[EndpointWeight] = weightService.getConnectionWeight(connection)
        assert(connectionWeights.isEmpty)
    }
  }

  test("inactive two endpoints should have weight of 100 points when loads") {
    val connectionService: ConnectionService = getConnectionService()
    connectionService.load("two_inactive") match {
      case Left(left) => fail(left)
      case Right(connection) =>
        val weightService: WeightService = getConnectionService().weightService
        val connectionWeights: List[EndpointWeight] = weightService.getConnectionWeight(connection)
        assert(connectionWeights.isEmpty)
    }
  }

  test("one active and one inactive should return only 1 endpoint and it has to have a weight of 100") {
    val connectionService: ConnectionService = getConnectionService()
    connectionService.load("one_active_one_inactive") match {
      case Left(left) => fail(left)
      case Right(connection) =>
        val weightService: WeightService = getConnectionService().weightService
        val connectionWeights: List[EndpointWeight] = weightService.getConnectionWeight(connection)
        assert(connectionWeights.lengthCompare(1) == 0)
        assert(connectionWeights.head.points.lengthCompare(100) == 0)
    }
  }
}
