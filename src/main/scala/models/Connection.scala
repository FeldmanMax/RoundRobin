package models

case class Connection(info: ConnectionGeneralInfo, endpoints: Map[String, ConnectionEndpoint]) {
	def totalWeight: Int = endpoints.values.map(x=>x.totalWeight).sum
}
case class ConnectionGeneralInfo(name: String)
case class ConnectionEndpoint(name: String, value: String, weight: Weight) {
	def totalWeight: Int = weight.size
}