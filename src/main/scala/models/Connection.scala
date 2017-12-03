package models

import com.fasterxml.jackson.annotation.JsonProperty

case class Connection(@JsonProperty("info") 					info: 					ConnectionGeneralInfo,
											@JsonProperty("endpointsList") 	endpointsList: 	List[ConnectionEndpoint]) {
	def key: String = info.name
	lazy val endpoints: Map[String, ConnectionEndpoint] = endpointsList.map(x=>x.name -> x).toMap
}
case class ConnectionGeneralInfo(name: String)
case class ConnectionEndpoint(name: String, value: String)
final case class ConnectionResponse(connectionName: String, endpointName: String, value: String)