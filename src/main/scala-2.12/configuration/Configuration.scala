package configuration

import com.typesafe.config._
import utils.ConnectionGenerator

package object ConfigUtils {
	implicit class ConfigExtension(config: Config) {
		def getData[TResult](key: String, dataType: String, default: TResult) : TResult = {
			if(!config.hasPath(key)){
				default
			}
			else{
				dataType match {
					case "String" => config.getString(key).asInstanceOf[TResult]
					case "Int" => config.getInt(key).asInstanceOf[TResult]
					case "Boolean" => config.getBoolean(key).asInstanceOf[TResult]
				}
			}
		}

		def getConfigOption[TResult](key: String): Option[TResult] = {
			if(!config.hasPath(key)) {
				None
			}
			else Some(config.getConfig(key).asInstanceOf[TResult])
		}
	}
}

object Configuration {
	import ConfigUtils.ConfigExtension
	import utils.Operators.OperatorsExtensions
	import utils.DataStructure.DataStructureSeqExtenstions

	private var _appConfig: Config = ConfigFactory.load()
	private var _connectionsConfig: Config = ConfigFactory.load("connections.conf")

	private var _connections: List[ConnectionConfigurationElement] = List.empty
	private var _connectionGenerator = new ConnectionGenerator()

	lazy val connectionRegion: String = _appConfig.getData("region", "String", "")

	def getConnectionsByDependencies : List[ConnectionConfigurationElement] = {
		val engine: ConnectionDependencyEngine = new ConnectionDependencyEngine
		engine.getConnectionsByDependencies(getConnections)
	}

	def getConnections : List[ConnectionConfigurationElement] = {
		if(_connections.isEmpty) {
			val connections = _connectionsConfig.getConfigList("connections")
			val listOfConnections = scala.collection.mutable.ArrayBuffer.empty[ConnectionConfigurationElement]
			connections.forEach(x => listOfConnections += createSingleConnectionElement(x))
			_connections = listOfConnections.toList
		}
		_connections
	}

	def getConnectionByName(name: String) : Option[ConnectionConfigurationElement] = {
		getConnections.filterHead((x) => x.name == name)
	}

	def setAppConfig(config: Config): Unit = _appConfig = config
	def setConnectionsConfig(config: Config): Unit = _connectionsConfig = config

	private def createSingleConnectionElement(config: Config): ConnectionConfigurationElement = {
		val name: String = config.getData("name", "String", "")
		val timeoutInMillis: Int = config.getData("timeoutInMillis", "Int", -1)
		val retries: Int = config.getData("retries", "Int", 3)
		val region: String = config.getData("region", "String", "")
		val listOfEndpointConfigurationElements = scala.collection.mutable.ArrayBuffer.empty[EndpointsConfigurationElement]
		config.getConfigList("endpoints").forEach(singleEndpointConfig => listOfEndpointConfigurationElements += createEndpointPerRegion(singleEndpointConfig, region))
		val endpointConfiguration = listOfEndpointConfigurationElements.toList
		ConnectionConfigurationElement(name, region, timeoutInMillis, retries, endpointConfiguration,
			createConnectionLimitations(config.getConfig("limitations")),
			createConnectionActions(config))
	}

	private def isComplexEndpoint(config: Config) : Boolean = config.getData("connections", "String", "").nonEmpty

	private def createEndpointPerRegion(config: Config, connectionRegion: String) : EndpointsConfigurationElement = {
		val region: String = config.getData("region", "String", connectionRegion)
		val connections: List[String] = retrieveConnectionsFromConfiguration(config)
		EndpointsConfigurationElement(region, generateEndpoints(config), connections)
	}

	private def generateEndpoints(config: Config) : List[EndpointConfigurationElement] = {
		val listOfEndpointConfigurationElements = scala.collection.mutable.ArrayBuffer.empty[EndpointConfigurationElement]
		if(!config.hasPath("points")) List.empty
		else {
			config.getConfigList("points").forEach( point => listOfEndpointConfigurationElements += createEndpoint(point))
			listOfEndpointConfigurationElements.toList
		}
	}

	private def retrieveConnectionsFromConfiguration(config: Config) : List[String] = {
		val connections: String = config.getData("connections", "String", "")
		connections.isEmpty?(List.empty, connections.split(",").toList)
	}

	private def createEndpoint(config: Config) : EndpointConfigurationElement = {
		EndpointConfigurationElement(config.getData("name", "String", ""), config.getData("value", "String", ""))
	}

	private def createConnectionLimitations(config: Config) : ConnectionLimitations = {
		val minPointsAmount: Int = config.getData("minPointsAmount", "Int", 1)
		val maxPointsAmount: Int = config.getData("maxPointsAmount", "Int", 1)
		val increaseFunction: String = config.getData("increaseFunction", "String", "Linear")
		val decreaseFunction: String = config.getData("decreaseFunction", "String", "Exponential")
		val increaseRatio: Int  = config.getData("increaseRatio", "Int", 1)
		val decreaseRatio: Int = config.getData("decreaseRatio", "Int", 1)
		val priority: String = config.getData("priority", "String", "N/A")
		val minConnectionWeight: Int = config.getData("minConnectionWeight", "Int", -1)
		ConnectionLimitations(minPointsAmount, maxPointsAmount, increaseFunction, decreaseFunction, increaseRatio, decreaseRatio, priority, minConnectionWeight)
	}

	private def createConnectionActions(config: Config): ConnectionActionsElement = {
		val actions: Option[Config] = config.getConfigOption("actions")
		actions match {
			case Some(v) => ConnectionActionsElement(v.getData("actionType", "String", "default"),
	                                             v.getData("actionResolver", "String", ""),
	                                             v.getData("cachingInMillis", "Int", -1),
																							 v.getData("params", "String", ""))
				case None => ConnectionActionsElement("None", "", -1, "")
		}

	}
}
