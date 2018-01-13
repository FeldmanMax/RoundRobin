package api

import com.google.inject.{Guice, Injector}
import dependencyInjection.RoundRobinInjectionModule
import models.{ConnectionResponse, ConnectionWeight, EndpointWeight, WeightRate}
import services.ConnectionService

import scala.concurrent.Future

object ConnectionAPI {
  private val injector: Injector = Guice.createInjector(new RoundRobinInjectionModule)
  private lazy val connectionService: ConnectionService = injector.getInstance(classOf[ConnectionService])

  def next(connectionName: String): Either[String, ConnectionResponse] = connectionService.next(connectionName)
  def update(endpointName: String, weightRate: WeightRate): Either[String, EndpointWeight] = connectionService.update(endpointName, weightRate)

  def nextAsync(connectionName: String): Future[Either[String, ConnectionResponse]] = connectionService.nextAsync(connectionName)
  def updateAsync(endpointName: String, weightRate: WeightRate): Future[Either[String, EndpointWeight]] = connectionService.updateAsync(endpointName, weightRate)

  def connectionWeight(connectionName: String): Either[String, ConnectionWeight] = connectionService.connectionWeight(connectionName)
}
