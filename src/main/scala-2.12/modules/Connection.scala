package modules

import configuration.ConnectionLimitations
import data_modules.{Angle, Endpoints, RoundRobinDTO}
import utils.{AnglesGenerator, DistanceCalculator, Lock}
import weights.Weight

abstract class Connection {

	private val _lock = new Lock()

	protected val anglesGenerator: AnglesGenerator = new AnglesGenerator

	protected var regionToEndpoints: Map[String, Endpoints]
	protected def myRegionEndpoints: Endpoints

	val connectionInformation: ConnectionInformation
	protected val increaseWeight: Weight
	protected val decreaseWeight: Weight
	protected val endpointsGenerator: EndpointsGenerator

	def name: String
	def overallPointsAmount: Int = myRegionEndpoints.endpoints.map(x=>x.points.length).sum
	def endpointsAmount: Int = myRegionEndpoints.endpoints.size
	def isNonPrimitiveConnection: Boolean = connectionInformation.configurationElement.hasNonPrimitiveConnections
	def connectionAngles: List[Angle] = connectionInformation.angles
	lazy val priority: String = connectionLimitations.priority
	lazy val connectionLimitations: ConnectionLimitations = connectionInformation.configurationElement.connectionLimitations
	val currentRegion: String

	def next(): RoundRobinDTO = {
		val result: data_modules.Endpoint = getClosestEndpoint(anglesGenerator.generateAngle())
		RoundRobinDTO(result.value, isSuccess = true, result.name, name)
	}

	def update(result: RoundRobinDTO): Unit = {
		if(isNonPrimitiveConnection) updateNonPrimitiveConnection(result) else updateSimpleConnection(result)
	}

	private def updateSimpleConnection(result: RoundRobinDTO) : Unit = {
		val endpoints: Map[String, Endpoints] = endpointsGenerator.regenerateEndpoints(regionToEndpoints, result, increaseWeight, decreaseWeight)
		try {
			_lock.acquire()
			regionToEndpoints = endpoints
		}
		finally {
			_lock.release()
		}
	}

	private def updateNonPrimitiveConnection(result: RoundRobinDTO) : Unit = {
		connectionInformation.angles = anglesGenerator.generateAngles(
			overallPointsAmount,
			connectionLimitations.minPointsAmount,
			connectionLimitations.maxPointsAmount,
			myRegionEndpoints.endpoints.size)
	}

	private def getClosestEndpoint(angle: Angle): data_modules.Endpoint = {
		try {
			_lock.acquire()
			DistanceCalculator.getClosestEndpoint(myRegionEndpoints, angle)
		}
		finally {
			_lock.release()
		}
	}
}