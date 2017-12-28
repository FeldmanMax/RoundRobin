package repositories

import cache.TimedCache
import models.EndpointWeight

class InMemoryWeightRepository (val weightsCache: TimedCache[String, EndpointWeight]) extends WeightRepository {
  def get(name: String, default: Option[EndpointWeight] = None): Either[String, EndpointWeight] = {
    default match {
      case None => weightsCache.getWithError(name)
      case Some(weight) => weightsCache.getOrAdd(name, weight)
    }
  }

  def updateWeight(weightName: String, weight: EndpointWeight): Either[String, EndpointWeight] = {
    weightsCache.put(weightName, weight)
    Right(weight)
  }
}
