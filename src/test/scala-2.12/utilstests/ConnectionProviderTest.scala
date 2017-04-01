package utilstests

import com.typesafe.config.{Config, ConfigFactory}
import configuration.Configuration
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
			assert(result.endpointName.contains("cspider__"), "Test1")
		}
	}

	test("Simple Endpoints - reduce to minimum") {
		val connection = ConnectionsContainer.getConnection("CSPider_SimpleEndpoints")

		for(index <- 1 to 10000) {
			val result: RoundRobinDTO = connection.next()
			val response: RoundRobinDTO = generateResponse(result, false)
			connection.update(response)
			assert(result.endpointName.contains("cspider__"), "Test1")
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

	test("AGCDB - get next to 2 points and go up to 200 points") {
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

	test("AGCDB - all connections are responding") {
		val connection = ConnectionsContainer.getConnection("AGCDB_EqualPriority")
		var isHK: Boolean = false
		var isSG: Boolean = false
		for(index <- 1 to 200) {
			val result: RoundRobinDTO = connection.next()
			if(result.connectionName.toLowerCase.contains("hk"))
				isHK = true
			if(result.connectionName.toLowerCase.contains("sg"))
				isSG = true
		}

		assert(isHK && isSG, "Not both endpoints returned")
	}

	test("AGCDB - all connections are responding with 50% for each endpoint when the endpoints are on 100%") {
		val connection = ConnectionsContainer.getConnection("AGCDB_EqualPriority")
		var hkAmount: Int = 0
		var sgAmount: Int = 0
		for(index <- 1 to 200) {
			val result: RoundRobinDTO = connection.next()
			if(result.connectionName.toLowerCase.contains("hk"))
				hkAmount = hkAmount + 1
			if(result.connectionName.toLowerCase.contains("sg"))
				sgAmount = sgAmount + 1
		}

		assert(sgAmount >= 80 && sgAmount <= 120)
		assert(hkAmount >= 80 && hkAmount <= 120)
	}

	test("AGCDB - get next by region") {
		val connection = ConnectionsContainer.getConnection("AGCDB")
		for(index <- 1 to 200) {
			val result: RoundRobinDTO = connection.next()
			val response: RoundRobinDTO = generateResponse(result, false)
			connection.update(response)
		}

		assert(connection.overallPointsAmount == 2, "was not reduced to 1% and now is " + connection.overallPointsAmount)

	}

	/*
	This test can run ONLY alone
	 */
	ignore("AGCDB - SG is the main DC and the result should be amsterdam - application/connections_001.conf") {
		val appConfig: Config = ConfigFactory.load("application_001.conf")
		val connectionsConfig: Config = ConfigFactory.load("connections_001.conf")
		Configuration.setAppConfig(appConfig)
		Configuration.setConnectionsConfig(connectionsConfig)
		val connection = ConnectionsContainer.getConnection("AGCDB")
		val result = connection.next()
		assert(result.endpointName.toLowerCase.contains("cspider_am"))
	}

	private def generateResponse(request: RoundRobinDTO, isSuccess: Boolean) : RoundRobinDTO = {
		RoundRobinDTO(request.destination, isSuccess, request.endpointName, request.connectionName)
	}
}
