package services

import models._
import repositories.ConnectionRepository

class ConnectionService(val weightService: WeightService,
                        val connectionRepository: ConnectionRepository,
                        val configurationService: ConfigurationService) {

	def load(connectionName: String): Either[String, Connection] = {
		configurationService.loadConnection(connectionName)
	}

  def next(connectionName: String): Either[String, ConnectionResponse] = {
    connectionRepository.get(connectionName) match {
      case Left(_) =>
        load(connectionName) match {
          case Left(left)         => Left(left)
          case Right(connection)  => connectionRepository.add(connection.key, connection)
                                     next(connection)
        }
      case Right(connection) => next(connection)
    }
  }

	def next(connection: Connection): Either[String, ConnectionResponse] = {
    val weightsList: List[Weight] = connectionWeightList(connection)
		val nextResult: Weight = weightService.next(weightsList)
		connection.endpoints.get(nextResult.name) match {
			case None => Left(s"Endpoint ${nextResult.name} was not found on the connection")
			case Some(endpoint) => Right(ConnectionResponse(connection.info.name, endpoint.name, endpoint.value))
		}
	}

  def update(endpointName: String, weightRate: WeightRate): Either[String, Weight] = {
    weightService.updateWeight(endpointName, weightRate)
	}

  def getWeight(connection: Connection): Int = {
    connection.endpoints.map { case(_, endpoint) =>
      weightService.getOrDefault(endpoint.name) match {
        case Left(_) => 100
        case Right(right) =>
          val size = right.size
          size
      }
    }.sum
  }

  private def connectionWeightList(connection: Connection): List[Weight] = {
    connection.endpoints.keys.map { endpointName =>
      weightService.getOrDefault(endpointName, Option(weightService.create(endpointName)))
    }.filter(x=>x.isRight).map(x=>x.right.get).toList
  }
}
