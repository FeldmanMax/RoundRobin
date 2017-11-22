package cache

object ConnectionsCache {
  import scala.concurrent.ExecutionContext.Implicits._
	private val connectionsCache = TimedCache.apply[String, String]()
  def connectionCache = connectionsCache
}

class TimedCache[Key<:Object, Value<:Object](val concurrencyLevel: Int=4, val timeoutMinutes: Int=5)
																						(implicit ec: scala.concurrent.ExecutionContext) {
	import java.util.concurrent.{Callable, TimeUnit}
	import com.google.common.cache.{Cache, CacheBuilder}
	import scala.concurrent.Future

	lazy val gCache: Cache[Key, Value] = CacheBuilder.newBuilder()
		.concurrencyLevel(concurrencyLevel)
		.softValues()
		.expireAfterWrite(timeoutMinutes, TimeUnit.MINUTES)
		.build[Key, Value]

	@inline def getWithDefault(key: Key, defaultValue: => Value): Value = gCache.get(key,
		new Callable[Value] {
			override def call: Value = defaultValue
		}
	)

	@inline def getAsyncWithDefault(key: Key, defaultValue: => Value): Future[Value] =
		Future { getWithDefault(key, defaultValue) }

	@inline def put(key: Key, value: Value): Unit = gCache.put(key, value)

	@inline def putAsync(key: Key, value: => Value): Future[Unit] = Future { gCache.put(key, value) }
}

object TimedCache {
	@inline def apply[Key<:Object, Value<:Object](concurrencyLevel: Int=4, timeoutMinutes: Int=5)
																							 (implicit ec: scala.concurrent.ExecutionContext) =
		new TimedCache[Key, Value](concurrencyLevel, timeoutMinutes){}
}