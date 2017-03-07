package configuration

import scala.annotation.tailrec

class ConnectionDependencyEngine {

	def getConnectionsByDependencies(connections: List[ConnectionConfigurationElement]) : List[ConnectionConfigurationElement] = {
		val primitiveConnections = connections.filter(x=>x.areOnlyPrimitiveConnections)
		val nonPrimitiveConnections = connections.filter(x=>x.hasNonPrimitiveConnections)
		generateSequanceByDependencies(primitiveConnections, nonPrimitiveConnections)
	}

	private def generateSequanceByDependencies(primitiveConnections: List[ConnectionConfigurationElement],
	                                           nonPrimitiveConnections: List[ConnectionConfigurationElement]) : List[ConnectionConfigurationElement] = {
		val resolvedConnections: List[ConnectionConfigurationElement] = addPrimitiveConnections(primitiveConnections)
		reorderComplexConnectionElements(resolvedConnections, nonPrimitiveConnections)
	}

	private def addPrimitiveConnections(primitiveConnections: List[ConnectionConfigurationElement]) : List[ConnectionConfigurationElement] = {
		List.empty ::: primitiveConnections
	}

	private def reorderComplexConnectionElements(exisitingAddedConnections: List[ConnectionConfigurationElement], complexConnections: List[ConnectionConfigurationElement])
		: List[ConnectionConfigurationElement] = {
		@tailrec
		def reorderComplexConnectionElements(complexConnections: List[ConnectionConfigurationElement],
		                                     collector: List[ConnectionConfigurationElement]) : List[ConnectionConfigurationElement] = {
			if(isFullyCollected(complexConnections.map(_.name), collector.map(_.name)))
				collector
			else {
				val addedConnections: List[ConnectionConfigurationElement] = complexConnections.flatMap(element => getResolvedConnections(collector, element))
				reorderComplexConnectionElements(complexConnections, (collector ::: addedConnections).distinct)
			}
		}
		reorderComplexConnectionElements(complexConnections, exisitingAddedConnections)
	}

	private def getResolvedConnections(collector: List[ConnectionConfigurationElement], element: ConnectionConfigurationElement) : List[ConnectionConfigurationElement] = {
		hasNonCollectedConnections(element.name, collector.map(_.name)) match {
			case true => addToCollectedConnections(collector, element)
			case false => List.empty
		}
	}

	private def addToCollectedConnections(collector: List[ConnectionConfigurationElement], potential: ConnectionConfigurationElement) : List[ConnectionConfigurationElement] = {
		hasNonCollectedConnections(potential.name, collector.map(_.name)) match {
			case true => potential :: collector
			case false => collector
		}
	}

	private def hasNonCollectedConnections(connection: String, collector: List[String]) : Boolean = !collector.contains(connection)
	private def isFullyCollected(connections: List[String], collector: List[String]) : Boolean = connections.forall(collector.contains)
}
