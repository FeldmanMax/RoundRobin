package utilstests

import configuration.{ConnectionActionsElement, ConnectionConfigurationElement, ConnectionLimitations, EndpointsConfigurationElement}
import data_modules.RoundRobinDTO
import modules.{Connection, ConnectionInformation, ConnectionsContainer}
import org.scalatest.FunSuite
import priorities.EqualPriority

class PriorityTest extends FunSuite {
	test("EqualPriority - 100000 rounds, with all success, should be equally distributed") {
		val connectionInformation: ConnectionInformation = generateBasicConnectionInformation()
		val equalPriority: EqualPriority = new EqualPriority(connectionInformation)
		val cspdr_hk:Connection = ConnectionsContainer.getConnection("CSPider_HK")
		val cspdr_sg:Connection = ConnectionsContainer.getConnection("CSPider_SG")

		for ( i <- 1 to 100000) {
			val result = equalPriority.getConnections(List(cspdr_hk, cspdr_sg))
			assert(result.size == 1, "Step 1")
		}
	}

	test("EqualPriority - Reduce HKG to 1% ==> Should get 2 connections") {
		val connectionInformation: ConnectionInformation = generateBasicConnectionInformation()
		val equalPriority: EqualPriority = new EqualPriority(connectionInformation)
		val cspdr_hk:Connection = ConnectionsContainer.getConnection("CSPider_HK")
		val cspdr_sg:Connection = ConnectionsContainer.getConnection("CSPider_SG")

		for ( i <- 1 to 200 ) {
			val response: RoundRobinDTO = cspdr_hk.next()
			cspdr_hk.update(RoundRobinDTO(response.destination, false, response.endpointName, response.connectionName))
		}

		for ( i <- 1 to 100000) {
			val result = equalPriority.getConnections(List(cspdr_hk, cspdr_sg))
			assert(result.size == 2, "Step 1")
		}
	}

	test("EqualPriority - Reduce HKG to 1% ==> Should get 2 connections ==> increase to 40% ==> should get 1 connection") {
		val connectionInformation: ConnectionInformation = generateBasicConnectionInformation()
		val equalPriority: EqualPriority = new EqualPriority(connectionInformation)
		val cspdr_hk:Connection = ConnectionsContainer.getConnection("CSPider_HK")
		val cspdr_sg:Connection = ConnectionsContainer.getConnection("CSPider_SG")

		for ( i <- 1 to 200 ) {
			val response: RoundRobinDTO = cspdr_hk.next()
			cspdr_hk.update(RoundRobinDTO(response.destination, false, response.endpointName, response.connectionName))
		}

		for ( i <- 1 to 100000) {
			val result = equalPriority.getConnections(List(cspdr_hk, cspdr_sg))
			assert(result.size == 2, "Step 1")
		}

		while (cspdr_hk.overallPointsAmount < 40) {
			val response: RoundRobinDTO = cspdr_hk.next()
			cspdr_hk.update(RoundRobinDTO(response.destination, true, response.endpointName, response.connectionName))
		}

		for ( i <- 1 to 100000) {
			val result = equalPriority.getConnections(List(cspdr_hk, cspdr_sg))
			assert(result.size == 1, "Step 2")
		}
	}

	private def generateBasicConnectionInformation() : ConnectionInformation = {
		val name: String = "s_name"
		val region: String = "HK"
		val timeoutInMillis: Int = 500
		val endpoints: List[EndpointsConfigurationElement] = List[EndpointsConfigurationElement] {
			EndpointsConfigurationElement("HK", List.empty, List.empty)
		}

		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(
			minPointsAmount=1,
			maxPointsAmount=100,
			increaseFunction="nevermind",
			decreaseFunction="nevermind",
			increaseRatio=0,
			decreaseRatio=0,
			priority= "nevermind",
			minConnectionWeight=20
		)
		val actions: ConnectionActionsElement = ConnectionActionsElement("actionType", "actionResolver", "params")
		val configurationElement: ConnectionConfigurationElement = ConnectionConfigurationElement(name, region, timeoutInMillis, endpoints, connectionLimitations, actions)
		val connectionInformation: ConnectionInformation = ConnectionInformation(configurationElement)
		connectionInformation
	}
}
