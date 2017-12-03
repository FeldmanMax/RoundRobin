package repositories

import cache.ConnectionsCache
import models.Weight

class WeightRepository() {

  def get(name: String, default: Option[Weight] = None): Either[String, Weight] = {
    default match {
      case None => ConnectionsCache.weightCache.getWithError(name)
      case Some(weight) => ConnectionsCache.weightCache.getOrAdd(name, weight)
    }
  }
}
