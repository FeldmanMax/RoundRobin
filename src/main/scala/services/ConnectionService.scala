package services

import com.google.inject.Inject
import com.google.inject.name.Named
import logging.Logger
import models._
import repositories.ConnectionRepository
import utils.ImplicitExecutionContext
import utils.Implicits.ListExtension

import scala.concurrent.Future

class ConnectionService @Inject() ( @Named("weight_service")        val weightService: WeightService,
                                    @Named("connection_repository") val connectionRepository: ConnectionRepository,
                                    @Named("config_service")        val configurationService: ConfigurationService) {

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
    Logger.info(s"${this.getClass} -> next")
    connectionRepository.get(connectionName) match {
      case Left(_) =>           load(connectionName).right.flatMap { connection => nextConnection(connection) }
      case Right(connection) => nextConnection(connection)
    }
  }

  def update(endpointName: String, weightRate: WeightRate): Either[String, EndpointWeight] = {
    val result = weightService.updateWeight(endpointName, weightRate)
    Logger.info(s"${result.toString}")
    result
  }

  def nextAsync(connectionName: String): Future[Either[String, ConnectionResponse]] = {
    Future { next(connectionName) }(ImplicitExecutionContext.RoundRobinExecutionContext)
  }

  def updateAsync(endpointName: String, weightRate: WeightRate): Future[Either[String, EndpointWeight]] = {
    Future { update(endpointName, weightRate) }(ImplicitExecutionContext.RoundRobinExecutionContext)
  }

  def connectionWeight(name: String): Either[String, ConnectionWeight] = {
    getConnection(name) match {
      case Left(left) => Left(left)
      case Right(connection) =>
        getConnectionActiveEndpoints(connection).right.flatMap { endpoints =>
          val totalWeight = endpoints.map(x=>x.size).sum
          val endpointToWeight = endpoints.map(x=>x.endPointName -> x).toMap
          Right(ConnectionWeight(totalWeight, endpointToWeight))
        }
    }
  }

  private def getConnectionActiveEndpoints(connection: Connection): Either[String, List[EndpointWeight]] = {
    if(!connection.info.isUsingConnections) Right(weightService.getConnectionWeight(connection))
    else  {
      val endpointsResult: List[Either[String, Either[String, List[EndpointWeight]]]] =
      connection.endpointsList.map { endpoint =>
        getConnection(endpoint.value).right.flatMap { childConn =>
          Right(getConnectionActiveEndpoints(childConn))
        }
      }

      if(endpointsResult.exists(x=>x.isLeft)) Left(endpointsResult.eitherMessage())
      else  {
        val weightsEither: List[Either[String, List[EndpointWeight]]] = endpointsResult.map(x=>x.right.get)
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
    getConnectionActiveEndpoints(connection).right.flatMap { endpoints =>
      val nextEndpoint: EndpointWeight = weightService.next(endpoints)
      connectionRepository.getByEndpoint(nextEndpoint.endPointName).right.flatMap { nextConn =>
        nextConn.endpoints.get(nextEndpoint.endPointName) match {
          case None => Left(s"Endpoint ${nextEndpoint.endPointName} was not found on the connection")
          case Some(endpoint) => Right(ConnectionResponse(connection.info.name, nextConn.info.name, endpoint.name, endpoint.value))
        }
      }
    }
  }

  private def next(connection: Connection): Either[String, ConnectionResponse] = {
    val weightsList: List[EndpointWeight] = weightService.getConnectionWeight(connection)
    val nextResult: EndpointWeight = weightService.next(weightsList)
    connection.endpoints.get(nextResult.endPointName) match {
      case None => Left(s"Endpoint ${nextResult.endPointName} was not found on the connection")
      case Some(endpoint) => Right(ConnectionResponse(connection.info.name, connection.info.name, endpoint.name, endpoint.value))
    }
  }
}