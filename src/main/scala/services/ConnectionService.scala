package services

import models._
import repositories.ConnectionRepository
import utils.Implicits.ListExtension

class ConnectionService(val weightService: WeightService,
                        val connectionRepository: ConnectionRepository,
                        val configurationService: ConfigurationService) {

	def load(connectionName: String): Either[String, Connection] = {
		configurationService.loadConnection(connectionName).right.flatMap { connection =>
      connectionRepository.add(connectionName, connection)
      connectionRepository.get(connectionName)
    }
	}

  def getConnection(name: String): Either[String, Connection] = {
    connectionRepository.get(name) match {
      case Right(connection) => Right(connection)
      case Left(_) => load(name)
    }
  }

  def getConnections(names: List[String]): Either[String, List[Connection]] = {
    val errorsToConnections: (List[String], List[Connection]) = names.map(getConnection).foldRight((List(""), List.empty[Connection])) {
      case (collector, (leftResult, rightResult)) =>
        collector.fold(left => (left :: leftResult, rightResult), right => (leftResult, right :: rightResult))
    }

    if(errorsToConnections._1.nonEmpty) Left(errorsToConnections._1 mkString "\n")
    else                                Right(errorsToConnections._2)
  }

  def next(connectionName: String): Either[String, ConnectionResponse] = {
    connectionRepository.get(connectionName) match {
      case Left(_) =>
        load(connectionName) match {
          case Left(left)         => Left(left)
          case Right(connection)  => nextConnection(connection)
        }
      case Right(connection) => nextConnection(connection)
    }
  }

	def next(connection: Connection): Either[String, ConnectionResponse] = {
    val weightsList: List[Weight] = weightService.getConnectionWeight(connection)
		val nextResult: Weight = weightService.next(weightsList)
		connection.endpoints.get(nextResult.endPointName) match {
			case None => Left(s"Endpoint ${nextResult.endPointName} was not found on the connection")
			case Some(endpoint) => Right(ConnectionResponse(connection.info.name, connection.info.name, endpoint.name, endpoint.value))
		}
	}

  def update(endpointName: String, weightRate: WeightRate): Either[String, Weight] = weightService.updateWeight(endpointName, weightRate)

  def connectionWeight(name: String): Either[String, ConnectionWeight] = {
    getConnection(name).right.flatMap { connection =>
      getConnectionActiveEndpoints(connection)
    }.right.flatMap { endpoints =>
      val totalWeight = endpoints.map(x=>x.size).sum
      val endpointToWeight = endpoints.map(x=>x.endPointName -> x).toMap
      Right(ConnectionWeight(totalWeight, endpointToWeight))
    }
  }

  private def getConnectionActiveEndpoints(connection: Connection): Either[String, List[Weight]] = {
    if(!connection.info.isUsingConnections) Right(weightService.getConnectionWeight(connection))
    else  {
      val endpointsResult: List[Either[String, Either[String, List[Weight]]]] =
      connection.endpointsList.map { endpoint =>
        connectionRepository.get(endpoint.value).right.flatMap { childConn =>
          Right(getConnectionActiveEndpoints(childConn))
        }
      }

      if(endpointsResult.exists(x=>x.isLeft)) Left(endpointsResult.eitherMessage())
      else  {
        val weightsEither: List[Either[String, List[Weight]]] = endpointsResult.map(x=>x.right.get)
        if(weightsEither.exists(x=>x.isLeft)) Left(weightsEither.eitherMessage())
        else                                  Right(weightsEither.flatten(x=>x.right.get))
      }
    }
  }

  private def nextConnection(connection: Connection): Either[String, ConnectionResponse] = {
    if(!connection.info.isUsingConnections) next(connection)
    else                                    nextConnectionImpl(connection)
  }

  private def nextConnectionImpl(connection: Connection): Either[String, ConnectionResponse] = {
    next(connection).right.flatMap { response =>
      getConnection(response.connectionName).right.flatMap { nextConn =>
        nextConn.endpoints.get(response.endpointName) match {
          case None => Left("Endpoint was not found")
          case Some(endpoint) => getConnection(endpoint.value).right.flatMap { currentActiveConnection =>
            nextConnection(currentActiveConnection).right.flatMap { conn =>
              Right(conn.copy(parentConnectionName = connection.info.name))
            }
          }
        }
      }
    }
  }
}