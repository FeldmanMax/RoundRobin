package repositories

import cache.TimedCache
import models.Connection

class ConnectionRepository(val cache: TimedCache[String, Connection]) {

  def get(name: String): Either[String, Connection] = {
    cache.getWithError(name)
  }

  def getOrAdd(name: String, func: => Connection) = {
    cache.getOrAdd(name)(func)
  }

  def add(key: String, connection: Connection): Unit = {
    cache.put(connection.key, connection)
  }

  def getByEndpoint(endpoint: String): Either[String, Connection] = {
    cache.getAll().right.flatMap { connections =>
      connections.find(connection => connection.endpointNames.contains(endpoint)) match {
        case None => Left(s"Connection for endpoint $endpoint not found")
        case Some(conn) => Right(conn)
      }
    }
  }
}
