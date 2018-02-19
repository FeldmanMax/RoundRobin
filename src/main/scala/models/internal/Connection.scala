package models.internal

final case class Connection(info: 					ConnectionGeneralInfo,
														endpointsList: 	List[ConnectionEndpoint]) {
	def key: String = info.name
	lazy val endpoints: Map[String, ConnectionEndpoint] = endpointsList.map(x=>x.name -> x).toMap
	lazy val endpointNames: List[String] = endpointsList.map(x=>x.name)
	lazy val activeEndpointNames: List[String] = endpointsList.filter(_.is_active).map(x=>x.name)
}
case class ConnectionGeneralInfo(name: String, isUsingConnections: Boolean = false, is_active: Boolean = true)
case class ConnectionEndpoint(name: String, value: String, is_active: Boolean)