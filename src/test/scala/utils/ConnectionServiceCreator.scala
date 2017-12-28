package utils

import cache.TimedCache
import models.{Connection, EndpointWeight}
import repositories.{ConnectionRepository, InMemoryWeightRepository, WeightRepository}
import services.{ConnectionService, PointsService, WeightService}

import scala.concurrent.ExecutionContext.Implicits.global

trait ConnectionServiceCreator extends ConfigurationServiceCreator {
  def getConnectionService(weightsCache: Option[TimedCache[String, EndpointWeight]] = None,
                           connectionCache: Option[TimedCache[String, Connection]] = None): ConnectionService = {
    val connCache: TimedCache[String, Connection] = connectionCache.getOrElse(TimedCache.apply[String, Connection]())
    new ConnectionService(
      getWeightService(weightsCache), new ConnectionRepository(connCache), configServiceWithFileConfiguration()
    )
  }

  private def getWeightService(weightsCache: Option[TimedCache[String, EndpointWeight]]): WeightService = {
    val weightRepository: WeightRepository = new InMemoryWeightRepository(weightsCache.getOrElse(TimedCache.apply[String, EndpointWeight]()))
    new WeightService(new PointsService(), weightRepository)
  }
}
