package repositories

import cache.TimedCache
import models.Weight

class InMemoryWeightRepository (val weightsCache: TimedCache[String, Weight]) extends WeightRepository {
  def get(name: String, default: Option[Weight] = None): Either[String, Weight] = {
    default match {
      case None => weightsCache.getWithError(name)
      case Some(weight) => weightsCache.getOrAdd(name, weight)
    }
  }

  def updateWeight(weightName: String, weight: Weight): Either[String, Weight] = {
    weightsCache.put(weightName, weight)
    Right(weight)
  }
}
