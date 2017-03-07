package modules
import configuration.ConnectionLimitations
import data_modules.RoundRobinDTO
import utils.{AnglesNormalizer, DistanceCalculator, Lock}

abstract class ConnectionGroup extends Connection {

	private val _lock = new Lock()

	private lazy val regionToConnections: Map[String, List[Connection]] = regionToEndpoints.map(x => x._1 -> x._2.connections)
	private lazy val myRegionConnections: List[Connection] = regionToConnections(connectionInformation.region.regionToUse)
	private lazy val connectionNameToConnection: Map[String, Connection] = myRegionConnections.map(x=> x.name -> x).toMap

	override def next(): RoundRobinDTO = {
		val connection = getConnection
		val result = connection.next()
		result
	}

	override def update(result: RoundRobinDTO): Unit = {
		val connection: Connection = connectionNameToConnection(result.connectionName)
		_lock.acquire()
		connection.update(result)
		updateConnectionGroup(result)
		_lock.release()
	}

	override def overallPointsAmount: Int = {
		val connections: List[Connection] = determineConnectionsByPriority()
		val connectionsAmount: Int = connections.map(_.endpointsAmount).sum
		val overallSize: Int = connections.map(_.overallPointsAmount).sum
		val amount: Int = AnglesNormalizer.normalize(overallSize, connectionsAmount, connectionInformation.configurationElement.connectionLimitations)
		amount
	}

	private def getConnection : Connection = {
		_lock.acquire()
		val connection = DistanceCalculator.getClosestConnection(determineConnectionsByPriority(), anglesGenerator.generateAngle())
		_lock.release()
		connection
	}

	private def determineConnectionsByPriority() : List[Connection] = {
		val regionalConnections: List[Connection] = myRegionEndpoints.connections

		priority match {
			case "region" => regionalConnections.filter(x=>connectionInformation.region.regionToUse == x.connectionInformation.configurationElement.region)
			case "equal" => myRegionEndpoints.connections
			case _ => throw new IllegalStateException(s"The priority $priority is not supported. Please check your configuration file")
		}
	}

	private def updateConnectionGroup(result: RoundRobinDTO): Unit = {
		val limitations: ConnectionLimitations = connectionInformation.configurationElement.connectionLimitations
		val endpointsAmount: Int = determineConnectionsByPriority().size
		connectionInformation.angles = anglesGenerator.generateAngles(overallPointsAmount, limitations.minPointsAmount, limitations.maxPointsAmount, endpointsAmount)
	}
}
