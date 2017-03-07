package modules

import com.sun.media.sound.InvalidDataException
import configuration.{Configuration, ConnectionConfigurationElement}
import utils.ConnectionGenerator

object ConnectionsContainer {

	private val _connections: Map[String, Connection] = generateConnections

	def getConnection(name: String) : Connection = {
		if(_connections.contains(name))
			_connections.get(name).get
		else
			throw new InvalidDataException("The connection " + name + " does not exist in the configuration")
	}

	private def generateConnections : Map[String, Connection] = {
		val connectionsToCreate: List[ConnectionConfigurationElement] = Configuration.getConnectionsByDependencies
		val connectionGenerator: ConnectionGenerator = new ConnectionGenerator
		val connections: List[Connection] = connectionGenerator.generateConnections(connectionsToCreate)
		connections.map(x=>(x.name, x)).toMap
	}
}
