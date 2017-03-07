package utilstests

import data_modules.RoundRobinDTO
import modules.ConnectionsContainer
import org.scalatest.FunSuite

class ConnectionProviderTest extends FunSuite {
	test("Equal endpoints distribution") {
		val connection = ConnectionsContainer.getConnection("CSPider_SimpleEndpoints")
		val results = scala.collection.mutable.ArrayBuffer.empty[(String, Int)]
		for(index <- 1 to 100000) {
			val result: RoundRobinDTO = connection.next()
			results += (result.endpointName -> 1)
		}
		val endpointsToAmmount: Map[String, Int] = results.groupBy(x=>x._1).map(x=> x._1 -> x._2.map(y=>y._2).sum)
		assert(endpointsToAmmount.size == 2, "Test1")
		assert(endpointsToAmmount.head._2 > 45000 && endpointsToAmmount.head._2 < 55000, "Test2")
		assert(endpointsToAmmount.tail.head._2 > 45000 && endpointsToAmmount.tail.head._2 < 55000, "Test3")
	}

	test("Simple Endpoints - 10000 requests should return the same endpoint") {
		val connection = ConnectionsContainer.getConnection("CSPider_SimpleEndpoints")

		for(index <- 1 to 10000) {
			val result: RoundRobinDTO = connection.next()
			assert(result.endpointName.contains("cspider_hk_"), "Test1")
		}
	}

	test("Simple Endpoints - reduce to minimum") {
		val connection = ConnectionsContainer.getConnection("CSPider_SimpleEndpoints")

		for(index <- 1 to 10000) {
			val result: RoundRobinDTO = connection.next()
			val response: RoundRobinDTO = generateResponse(result, false)
			connection.update(response)
			assert(result.endpointName.contains("cspider_hk_"), "Test1")
		}
		assert(connection.overallPointsAmount == 2, "Test2")
	}

	test("Simple Endpoints - reduce to minimum and then to maximum") {
		val connection = ConnectionsContainer.getConnection("CSPider_SimpleEndpoints")

		for(index <- 1 to 10) {
			val result: RoundRobinDTO = connection.next()
			val response: RoundRobinDTO = generateResponse(result, false)
			connection.update(response)
		}

		for(index <- 1 to 10000) {
			connection.update(connection.next())
		}
		assert(connection.overallPointsAmount == 200, "Test2")
	}

	test("CSPider_SG - get next") {
		val connection = ConnectionsContainer.getConnection("CSPider_SG")
		val result = connection.next()
		assert(result.endpointName.contains("cspider_sg"))
	}

	test("CSPider_HK - get next") {
		val connection = ConnectionsContainer.getConnection("CSPider_HK")
		val result = connection.next()
		assert(result.endpointName.contains("cspider_hk"))
	}

	test("AGCDB - get next") {
		val connection = ConnectionsContainer.getConnection("AGCDB")
		for(index <- 1 to 200) {
			val result: RoundRobinDTO = connection.next()
			val response: RoundRobinDTO = generateResponse(result, false)
			connection.update(response)
		}

		assert(connection.overallPointsAmount == 2, "was not reduced to 1% and now is " + connection.overallPointsAmount)

		for(index <- 1 to 10000) {
			connection.update(connection.next())
		}

		assert(connection.overallPointsAmount == 100, "was not increased to 100%")
	}

	test("AGCDB - reduce to 1 and increase to 100") {
		val connection = ConnectionsContainer.getConnection("AGCDB")
		val result = connection.next()
		assert(result.endpointName.toLowerCase.contains("cspider_hk") || result.endpointName.toLowerCase.contains("cspider_sg"))
	}

	private def generateResponse(request: RoundRobinDTO, isSuccess: Boolean) : RoundRobinDTO = {
		RoundRobinDTO(request.destination, isSuccess, request.endpointName, request.connectionName)
	}
}
