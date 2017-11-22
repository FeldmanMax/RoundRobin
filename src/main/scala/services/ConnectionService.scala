package services

import models.{Connection, ConnectionEndpoint, Weight, WeightRate}

class ConnectionService(val weightService: WeightService) {

	def load(connectionName: String): Either[String, Connection] = {
		Right(null)
	}

	def next(connection: Connection): Either[String, ConnectionEndpoint] = {
		val nextResult: Weight = weightService.next(connection.endpoints.values.toList.map(x=>x.weight))
		connection.endpoints.get(nextResult.name) match {
			case None => Left(s"Endpoint ${nextResult.name} was not found on the connection")
			case Some(endpoint) => Right(endpoint)
		}
	}

	def update(connection: Connection, endpointName: String, weightRate: WeightRate): Either[String, Connection] = {
		connection.endpoints.get(endpointName) match {
			case None => Left(s"Endpoint $endpointName was not found on the connection")
			case Some(endpoint) =>
				val updatedWeight: Weight = weightService.updateWeight(endpoint.weight, weightRate)
				val nameToEndpoint: Map[String, ConnectionEndpoint] = connection.endpoints.filter { case (name, _) => name != endpoint.name } + (endpoint.name -> endpoint.copy(weight = updatedWeight))
				Right(connection.copy(endpoints = nameToEndpoint))
		}
	}
}
