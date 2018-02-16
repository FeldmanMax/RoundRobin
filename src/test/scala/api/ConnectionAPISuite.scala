package api

import org.scalatest.FunSuite
import roundrobin.api.ConnectionAPI
import roundrobin.models.api.WeightRate

class ConnectionAPISuite extends FunSuite {
  test("next with dependency injection") {
    ConnectionAPI.next("connection_with_4_simple_endpoints") match {
      case Left(_) => fail("Injection did not work")
      case Right(response) =>
        assert(List("endpoint_A", "endpoint_B", "endpoint_C", "endpoint_D").contains(response.endpointName),
              "No endpoint returned")
    }
  }

  test("update with dependency injection reduce twice the same endpoint (10 points) and then increase it (5 points)") {
    ConnectionAPI.update("endpoint_A", WeightRate(isSuccess = false, isPercent = false, quantity = 10))
    ConnectionAPI.update("endpoint_A", WeightRate(isSuccess = false, isPercent = false, quantity = 10))
    ConnectionAPI.connectionWeight("connection_with_4_simple_endpoints") match {
      case Left(_) => fail("Could not bring the connection weight")
      case Right(response) => assert(response.totalWeight == 380, "!= 380")
    }
    ConnectionAPI.update("endpoint_A", WeightRate(isSuccess = true, isPercent = false, quantity = 5))
    ConnectionAPI.connectionWeight("connection_with_4_simple_endpoints") match {
      case Left(_) => fail("Could not bring the connection weight")
      case Right(response) => assert(response.totalWeight == 385, "!= 385")
    }
  }
}
