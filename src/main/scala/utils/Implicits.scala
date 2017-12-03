package utils

import java.io.Closeable

import com.google.common.cache.CacheBuilder
import org.joda.time.DateTime

import scalacache.ScalaCache
import scalacache.guava.GuavaCache

object Implicits {
	implicit class DoubleExtension(double: Double) {
		def isEqual(other: Double, epsilon: Double): Boolean = Math.abs(double - other) < epsilon
	}

	private lazy val connectionCacheInstance = CacheBuilder.newBuilder().build[String, Object]()
	implicit val connectionCache = ScalaCache(new GuavaCache(connectionCacheInstance))

	object JodaOrdering {
		implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)
	}

	implicit class CloseableExtension(closable: Closeable) {
		def using[T](f: (Closeable) => T): Either[String, T] = {
			try {
				Right(f(closable))
			}
			catch {
				case ex: Exception => Left(ex.getMessage)
			}
			finally {
				closable.close()
			}
		}
	}
}
