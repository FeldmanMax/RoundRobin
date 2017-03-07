package data_modules

import modules.{Connection, ConnectionEndpoints}

case class Endpoints(region: String, endpoints: List[Endpoint], connectionEndpoints: Option[ConnectionEndpoints]) {
	def connections: List[Connection] = connectionEndpoints.fold(List.empty[Connection]) { _.connections }
}
