package priorities
import modules.{Connection, ConnectionInformation}

import scala.annotation.tailrec

class RegionalPriority(val connectionInformation: ConnectionInformation) extends Priority {

	private val minConnectionWeight: Int = connectionInformation.configurationElement.connectionLimitations.minConnectionWeight

	override def getConnections(connections: List[Connection]): List[Connection] = {
		val regionalConnection: Option[Connection] = connections.find(x=>x.connectionInformation.region.regionToUse == connectionInformation.region.regionToUse)
		regionalConnection match {
			case Some(connection) => getConnectionsByPriority(connection, connections)
			case None => connections
		}
	}

	private def getConnectionsByPriority(regionalConnection: Connection, connections: List[Connection]) : List[Connection] = {
		if (regionalConnection.overallPointsAmount > minConnectionWeight){
			List(regionalConnection)
		}
		else{
			getConnectionsByPriorityImpl(connections)
		}
	}

	private def getConnectionsByPriorityImpl(connections: List[Connection]) : List[Connection] = {
		@tailrec
		def getConnectionsByPriorityExecutor(connections: List[Connection], connectionsToTake: Int, maxConnections: Int) : List[Connection] = {
			if(connectionsToTake >= maxConnections) connections
			else {
				val selectedConnections: List[Connection] = connections.take(connectionsToTake)
				if(shouldContinueAddingConnections(selectedConnections))
					getConnectionsByPriorityExecutor(connections, connectionsToTake + 1, maxConnections)
				else
					selectedConnections
			}
		}
		getConnectionsByPriorityExecutor(connections, 1, connections.size)
	}

	private def shouldContinueAddingConnections(connections: List[Connection]) : Boolean = {
		val amountOfConnections: Int = connections.size
		(connections.map(_.overallPointsAmount).sum / amountOfConnections) < minConnectionWeight
	}
}
