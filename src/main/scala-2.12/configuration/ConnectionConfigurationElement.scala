package configuration


case class ConnectionConfigurationElement(name: String,
                                          region: String,
                                          connectionTimeoutInMillis: Int,
                                          commandTimeoutInMillis: Int,
                                          retries: Int,
                                          endpoints: List[EndpointsConfigurationElement],
                                          connectionLimitations: ConnectionLimitations,
                                          actions: ConnectionActionsElement) {
	import utils.DataStructure._

	def dependencyConnectionsNames : List[String] = {
		(endpoints.map(x=>x.connectionsCommaSeparated) mkString ",").split(",").toList.toListOrDefault(true)
	}

	def areOnlyPrimitiveConnections: Boolean = endpoints.isEmptyCondition((x) => x.hasConnections)
	def hasNonPrimitiveConnections: Boolean = endpoints.isNonEmptyCondition((x) => x.hasConnections)
}
