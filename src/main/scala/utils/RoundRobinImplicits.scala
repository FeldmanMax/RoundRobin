package utils

import java.io.Closeable

import com.google.common.cache.CacheBuilder
import org.joda.time.DateTime

object RoundRobinImplicits {
	implicit class DoubleExtension(double: Double) {
		def isEqual(other: Double, epsilon: Double): Boolean = Math.abs(double - other) < epsilon
	}

	private lazy val connectionCacheInstance = CacheBuilder.newBuilder().build[String, Object]()

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

	implicit class ListExtension[T](list: List[Either[String, T]]) {
		def eitherMessage(): String = {
			list.filter(x=>x.isLeft).map(x=>x.left.get) mkString "\n"
		}

		def allRightOrError(): Either[String, List[T]] = {
			val eitherMessage: String = this.eitherMessage()
			if(eitherMessage.isEmpty)	Right(list.map(x=>x.right.get))
			else											Left(eitherMessage)
		}
	}
}
