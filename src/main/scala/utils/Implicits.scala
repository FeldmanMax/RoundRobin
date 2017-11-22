package utils

import com.google.common.cache.CacheBuilder

import scalacache.ScalaCache
import scalacache.guava.GuavaCache

object Implicits {
	implicit class DoubleExtension(double: Double) {
		def isEqual(other: Double, epsilon: Double): Boolean = Math.abs(double - other) < epsilon
	}

	private lazy val connectionCacheInstance = CacheBuilder.newBuilder().build[String, Object]()
	implicit val connectionCache = ScalaCache(new GuavaCache(connectionCacheInstance))
}
