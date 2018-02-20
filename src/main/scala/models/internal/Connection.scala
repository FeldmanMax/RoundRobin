package models.internal

final case class Connection(info: 					ConnectionGeneralInfo,
														endpointsList: 	List[ConnectionEndpoint],
														metadata: 			ConnectionMetadata[String]) {
	def key: String = info.name
	lazy val endpoints: Map[String, ConnectionEndpoint] = endpointsList.map(x=>x.info.key -> x).toMap
	lazy val endpointNames: List[String] = endpointsList.map(x=>x.info.key)
	lazy val activeEndpointNames: List[String] = endpointsList.filter(_.is_active).map(x=>x.info.key)
}
case class ConnectionGeneralInfo(name: String, isUsingConnections: Boolean = false, is_active: Boolean = true)
case class ConnectionEndpoint(info: KeyValue[String], is_active: Boolean) {
	def name: String = info.key
	def value: String = info.value
}
case class ConnectionMetadata[T](list: List[KeyValue[T]]) {
	def hasMetadata: Boolean = list.nonEmpty
}
case class KeyValue[T](key: String, value: T) {
	override def toString: String = s"key: $key, value: ${value.toString}"
}