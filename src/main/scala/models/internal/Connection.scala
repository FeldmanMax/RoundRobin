package models.internal

final case class Connection(info: 					ConnectionGeneralInfo,
														endpointsList: 	List[ConnectionEndpoint]) {
	def key: String = info.name
	lazy val endpoints: Map[String, ConnectionEndpoint] = endpointsList.map(x=>x.name -> x).toMap
	lazy val endpointNames: List[String] = endpoints.keys.toList
}
case class ConnectionGeneralInfo(name: String, isUsingConnections: Boolean = false)
case class ConnectionEndpoint(name: String, value: String)