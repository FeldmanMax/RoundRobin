package modules

import data_modules.{Angle, Endpoint, Endpoints, RoundRobinDTO}
import utils.DistanceCalculator

class EndpointsContainer(val regionToEndpoints: Map[String, Endpoints]) {
	def getClosestEndpoint(angle: Angle, region: String): Endpoint = {
			DistanceCalculator.getClosestEndpoint(regionToEndpoints(region), angle)
	}

	def getClosestEndpointDTO(angle: Angle, region: String, connectionName: String): RoundRobinDTO = {
		val endpoint: Endpoint = getClosestEndpoint(angle, region)
		RoundRobinDTO(endpoint.value, isSuccess = true, endpoint.name, connectionName )
	}
}
