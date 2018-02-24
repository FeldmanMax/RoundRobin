package cache

import models.internal.Connection
import roundrobin.models.api.EndpointWeight

object ConnectionsCache {
  import scala.concurrent.ExecutionContext.Implicits._
	private val connectionsCache: TimedCache[String, Connection] = TimedCache.apply[String, Connection]()
  private val weightsCache: TimedCache[String, EndpointWeight] = TimedCache.apply[String, EndpointWeight]()
  def connectionCache = connectionsCache
  def weightCache = weightsCache
}