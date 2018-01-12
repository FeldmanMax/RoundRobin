package api

import com.google.inject.{Guice, Injector}
import dependencyInjection.RoundRobinInjectionModule
import models.{ConnectionResponse, ConnectionWeight, EndpointWeight, WeightRate}
import services.ConnectionService

object ConnectionAPI {
  private val injector: Injector = Guice.createInjector(new RoundRobinInjectionModule)
  private lazy val connectionService: ConnectionService = injector.getInstance(classOf[ConnectionService])

  def next(connectionName: String): Either[String, ConnectionResponse] = connectionService.next(connectionName)
  def update(endpointName: String, weightRate: WeightRate): Either[String, EndpointWeight] = connectionService.update(endpointName, weightRate)
  def connectionWeight(connectionName: String): Either[String, ConnectionWeight] = connectionService.connectionWeight(connectionName)
}
