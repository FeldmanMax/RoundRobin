package configuration

case class EndpointsConfigurationElement(region: String,
                                         endpoints: Seq[EndpointConfigurationElement],
                                         connections: List[String]) {
	import utils.Operators.OperatorsExtensions

	val hasConnections: Boolean = connections.nonEmpty
	val connectionsCommaSeparated: String = hasConnections ? (connections mkString ",", "")
}
