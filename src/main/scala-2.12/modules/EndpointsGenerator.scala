package modules

import com.sun.media.sound.InvalidDataException
import configuration._
import data_modules.{Endpoints, RoundRobinDTO}
import utils.AnglesGenerator
import weights.Weight

trait EndpointsGenerator {

	import utils.DataStructure.DataStructureSeqExtenstions
	import utils.Operators.OperatorsExtensions

	private val anglesGenerator: AnglesGenerator = new AnglesGenerator
	val connectionConfigurationElement: ConnectionConfigurationElement
	lazy val connectionLimitations: ConnectionLimitations = connectionConfigurationElement.connectionLimitations

	def generateEndpoints(endpoints: List[EndpointsConfigurationElement], existingConnection: Option[List[Connection]]) : Seq[Endpoints] = {
		val simpleConnections: Seq[EndpointsConfigurationElement] = endpoints.filterNot(x=>x.hasConnections)
		val complexConnections: Seq[EndpointsConfigurationElement] = endpoints.filter(x=>x.hasConnections)
		simpleConnections.map(x=>generateSimpleEndpointsImpl(x.endpoints, x.region)).toList :::
		complexConnections.map(x=>generateComplexEndpointsImpl(x, existingConnection)).toList
	}

	def regenerateEndpoints(allEndpoints: Map[String, Endpoints], result: RoundRobinDTO, increaseWeight: Weight, decreaseWeight: Weight) : Map[String, Endpoints] = {
		val endpoints = allEndpoints(Configuration.connectionRegion)
		val newEndpoints: Endpoints = shouldRegenerate(endpoints, result) ? (regeneratePointsImpl(result, endpoints, increaseWeight, decreaseWeight), endpoints)
		generateNewEndpointsCollection(allEndpoints, newEndpoints)
	}

	private def generateSimpleEndpointsImpl(endpoints: Seq[EndpointConfigurationElement], region: String) : Endpoints = {
		val amountOfPoints: Int = connectionConfigurationElement.connectionLimitations.maxPointsAmount
		Endpoints(region, endpoints.map(x => data_modules.Endpoint(x.name, x.value, anglesGenerator.generateAngles(amountOfPoints))).toList, None)
	}

	private def generateComplexEndpointsImpl(endpointsConfigElement: EndpointsConfigurationElement, existingConnections: Option[List[Connection]]) : Endpoints = {
		if(existingConnections.isEmpty) throw new InvalidDataException("existing connections are empty")
		val connections: List[Connection] = endpointsConfigElement.connections.map(x=>getConnection(x, existingConnections.getOrElse(List.empty[Connection]))).filter(x=>x.nonEmpty).map(x=>x.get)
		Endpoints(endpointsConfigElement.region, List.empty, Some(ConnectionEndpoints(connections)))
	}

	private def getConnection(connectionName: String, existingConnections: List[Connection]) : Option[Connection] = {
		existingConnections.find(x=>x.name == connectionName)
	}

	private def shouldRegenerate(endpoints: Endpoints, roundRobinDTO: RoundRobinDTO) : Boolean = {
		val endpoint = getEndpointByName(endpoints, roundRobinDTO.endpointName)
		endpoint match {
			case None => false
			case _ => shouldRegenerateImpl(endpoint.get, roundRobinDTO)
		}
	}

	private def shouldRegenerateImpl(endpoint: data_modules.Endpoint, roundRobinDTO: RoundRobinDTO) : Boolean = {
		val isSuccessAndShouldIncrease = roundRobinDTO.isSuccess && !endpoint.isTopAnglesAmount(connectionLimitations.maxPointsAmount)
		val isFailureAndShouldDecrease = !roundRobinDTO.isSuccess && !endpoint.isBottomAnglesAmount(connectionLimitations.minPointsAmount)
		isSuccessAndShouldIncrease || isFailureAndShouldDecrease
	}

	private def getEndpointByName(endpoints: Endpoints, name: String) : Option[data_modules.Endpoint] = {
		endpoints.endpoints.filterHead((x) => x.name == name)
	}

	private def regeneratePointsImpl(roundRobinDTO: RoundRobinDTO, endpoints: Endpoints, increaseWeight: Weight, decreaseWeight: Weight) : Endpoints = {
		val newEndpoint: data_modules.Endpoint = regenerateSingleEndpoint(roundRobinDTO, endpoints, increaseWeight, decreaseWeight)
		val result = endpoints.endpoints.map(x=>generateEndpoint(x, newEndpoint))
		Endpoints(endpoints.region, result, None)
	}

	private def regenerateSingleEndpoint(roundRobinDTO: RoundRobinDTO, endpoints: Endpoints, increaseWeight: Weight, decreaseWeight: Weight): data_modules.Endpoint = {
		if (roundRobinDTO.isSuccess)
			increaseWeight.increase(getEndpointByName(endpoints, roundRobinDTO.endpointName).get)
		else
			decreaseWeight.decrease(getEndpointByName(endpoints, roundRobinDTO.endpointName).get)
	}

	private def generateEndpoint(oldEndpoint: data_modules.Endpoint, newEndpoint: data_modules.Endpoint) : data_modules.Endpoint = {
		(oldEndpoint.name == newEndpoint.name) ? (newEndpoint, oldEndpoint)
	}

	private def generateNewEndpointsCollection(old: Map[String, Endpoints], newEndpoints: Endpoints): Map[String, Endpoints] = {
		old.map(x=> x._1 -> generateEndpoints(x._2, newEndpoints))
	}

	private def generateEndpoints(oldEndpoints: Endpoints, newEndpoints: Endpoints) : Endpoints = {
		(oldEndpoints.region == newEndpoints.region) ? (newEndpoints, oldEndpoints)
	}
}