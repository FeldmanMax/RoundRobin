package repositories

import cache.TimedCache
import models.Connection

class ConnectionRepository(val cache: TimedCache[String, Connection]) {

  def get(name: String): Either[String, Connection] = {
    cache.getWithError(name)
  }

  def add(key: String, connection: Connection): Unit = {
    cache.put(connection.key, connection)
  }
}
