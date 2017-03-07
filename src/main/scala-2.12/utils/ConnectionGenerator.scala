package utils

import configuration.{Configuration, ConnectionConfigurationElement}
import data_modules.Endpoints
import modules._
import weights.{Weight, WeightGenerator}
import utils.DataStructure.DataStructureSeqExtenstions

class ConnectionGenerator {
	def generateConnections(configurationElements: List[ConnectionConfigurationElement]) : List[Connection] = {
		val simpleConnections: List[Connection] = configurationElements.filterNot(x=>x.hasNonPrimitiveConnections).map(createSimpleConnection)
		val complexConnections: List[Connection] = configurationElements.filter(x=>x.hasNonPrimitiveConnections).map(x=>createComplexConnection(x, Some(simpleConnections)))
		simpleConnections ::: complexConnections
	}

	def generateSimpleConnections(configurationElements: List[ConnectionConfigurationElement]): List[Connection] = {
		val simpleConnections: List[Connection] = configurationElements.filterNot(x=>x.hasNonPrimitiveConnections).map(createSimpleConnection)
		simpleConnections
	}

	private def createSimpleConnection(configurationElement: ConnectionConfigurationElement) : Connection = {
		val weightGenerator: WeightGenerator = new WeightGenerator(configurationElement.connectionLimitations)
		val endpointsGenerator: EndpointsGenerator = new EndpointsGenerator {
			override val connectionConfigurationElement: ConnectionConfigurationElement = configurationElement
		}

		val endpoints: Map[String, Endpoints] = buildEndpoints(endpointsGenerator, configurationElement, None)
		val connection = new Connection {
			override val connectionInformation: ConnectionInformation = ConnectionInformation(configurationElement)
			override protected val decreaseWeight: Weight = weightGenerator.generateDecrease()
			override protected val increaseWeight: Weight = weightGenerator.generateIncrease()
			override val currentRegion: String = Configuration.connectionRegion
			override protected val endpointsGenerator: EndpointsGenerator = new EndpointsGenerator {
				override val connectionConfigurationElement: ConnectionConfigurationElement = configurationElement
			}
			override protected var regionToEndpoints: Map[String, Endpoints] = endpoints
			override protected def myRegionEndpoints: Endpoints = regionToEndpoints(connectionInformation.region.regionToUse)
			override def name: String = connectionInformation.name
		}
		connection
	}

	private def createComplexConnection(configurationElement: ConnectionConfigurationElement, simpleConnections: Option[List[Connection]]) : Connection = {
		val weightGenerator: WeightGenerator = new WeightGenerator(configurationElement.connectionLimitations)
		val endpointsGenerator: EndpointsGenerator = new EndpointsGenerator {
			override val connectionConfigurationElement: ConnectionConfigurationElement = configurationElement
		}
		val endpoints: Map[String, Endpoints] = buildEndpoints(endpointsGenerator, configurationElement, simpleConnections)
		val connectionGroup = new ConnectionGroup {
			override val connectionInformation: ConnectionInformation = ConnectionInformation(configurationElement)
			override val currentRegion: String = configurationElement.region
			override protected val decreaseWeight: Weight = weightGenerator.generateDecrease()
			override protected val endpointsGenerator: EndpointsGenerator = new EndpointsGenerator {
				override val connectionConfigurationElement: ConnectionConfigurationElement = configurationElement
			}
			override protected val increaseWeight: Weight = weightGenerator.generateIncrease()
			override protected var regionToEndpoints: Map[String, Endpoints] = endpoints
			override def name: String = connectionInformation.name
			override protected def myRegionEndpoints: Endpoints = regionToEndpoints(connectionInformation.region.regionToUse)

		}
		connectionGroup
	}

	private def buildEndpoints(endpointsGenerator: EndpointsGenerator,
	                           configurationElement: ConnectionConfigurationElement,
	                           simpleConnections: Option[List[Connection]]) : Map[String, Endpoints] = {
		val endpoints: Map[String, Endpoints] = endpointsGenerator.
																						generateEndpoints(configurationElement.endpoints, simpleConnections).
																						toMapConditionted((x) => x.region -> x)
		endpoints
	}
}
