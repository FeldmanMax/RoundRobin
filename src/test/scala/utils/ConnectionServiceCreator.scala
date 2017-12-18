package utils

import cache.TimedCache
import models.{Connection, Weight}
import repositories.{ConnectionRepository, InMemoryWeightRepository, WeightRepository}
import services.{ConnectionService, PointsService, WeightService}

import scala.concurrent.ExecutionContext.Implicits.global

trait ConnectionServiceCreator extends ConfigurationServiceCreator {
  def getConnectionService(weightsCache: Option[TimedCache[String, Weight]] = None): ConnectionService = {
    new ConnectionService(
      getWeightService(weightsCache), new ConnectionRepository(cacheConnectionInstance), configServiceWithFileConfiguration()
    )
  }

  private def getWeightService(weightsCache: Option[TimedCache[String, Weight]]): WeightService = {
    val weightRepository: WeightRepository = new InMemoryWeightRepository(weightsCache.getOrElse(TimedCache.apply[String, Weight]()))
    new WeightService(new PointsService(), weightRepository)
  }

  private def cacheConnectionInstance: TimedCache[String, Connection] = {
    TimedCache.apply[String, Connection]()
  }
}
