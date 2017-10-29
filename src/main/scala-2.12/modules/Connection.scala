package modules

import configuration.{ConnectionConfigurationElement, ConnectionLimitations}
import data_modules.{Angle, Endpoints, RoundRobinDTO}
import utils.{AnglesGenerator, Lock}
import weights.Weight

abstract class Connection {

	private val _lock = new Lock()

	protected val anglesGenerator: AnglesGenerator = new AnglesGenerator

	protected var endpointsContainer: EndpointsContainer
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
	def connectionLimitations: ConnectionLimitations = connectionInformation.configurationElement.connectionLimitations
	val currentRegion: String

	def next(): RoundRobinDTO = getClosestEndpoint(anglesGenerator.generateAngle())
	def update(result: RoundRobinDTO): Unit = if(isNonPrimitiveConnection) updateNonPrimitiveConnection(result) else updateSimpleConnection(result)


	private def updateSimpleConnection(result: RoundRobinDTO) : Unit = {
		val endpoints: Map[String, Endpoints] = endpointsGenerator.regenerateEndpoints(endpointsContainer.regionToEndpoints, result, increaseWeight, decreaseWeight)
		try {
			_lock.acquire()
			endpointsContainer = new EndpointsContainer(endpoints)
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

	private def getClosestEndpoint(angle: Angle): RoundRobinDTO = {
		try {
			_lock.acquire()
			endpointsContainer.getClosestEndpointDTO(angle, connectionInformation.region.regionToUse, name, connectionInformation.configurationElement.connectionTimeoutInMillis, connectionInformation.configurationElement.commandTimeoutInMillis)
		}
		finally {
			_lock.release()
		}
	}
}