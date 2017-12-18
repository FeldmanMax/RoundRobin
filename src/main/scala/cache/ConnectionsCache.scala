package cache

import models.{Connection, Weight}

object ConnectionsCache {
  import scala.concurrent.ExecutionContext.Implicits._
	private val connectionsCache: TimedCache[String, Connection] = TimedCache.apply[String, Connection]()
  private val weightsCache: TimedCache[String, Weight] = TimedCache.apply[String, Weight]()
  def connectionCache = connectionsCache
  def weightCache = weightsCache
}

class TimedCache[Key<:Object, Value<:Object](val concurrencyLevel:  Int=6,
                                             val timeoutMinutes:    Int=10)
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

  @inline def getWithError(key: Key): Either[String, Value] = {
    try{
      val result = gCache.getIfPresent(key)
      if(result == null)  Left(s"Key $key was not found")
      else                Right(result)
    }
    catch {
      case ex: Exception => Left(ex.getMessage)
    }
  }

  @inline def getOrAdd(key: Key, default: Value): Either[String, Value] = {
    if(gCache.getIfPresent(key) == null) {
      put(key, default)
    }
    getWithError(key)
  }

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