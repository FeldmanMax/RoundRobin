package repositories

import cache.ConnectionsCache
import models.Connection

class ConnectionRepository() {

  def get(name: String): Either[String, Connection] = {
    ConnectionsCache.connectionCache.getWithError(name)
  }

  def add(key: String, connection: Connection): Unit = {
    ConnectionsCache.connectionCache.put(connection.key, connection)
  }
}
