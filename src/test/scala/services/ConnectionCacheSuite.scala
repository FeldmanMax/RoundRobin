package services

import cache.ConnectionsCache
import models.internal.Connection
import org.scalatest.FunSuite
import utils.ConnectionCreator

class ConnectionCacheSuite extends FunSuite with ConnectionCreator {
	test("add to cache") {
    val conn_a: Connection = getDefaultConnection(name = "t1")
    val conn_b: Connection = getDefaultConnection(name = "t2")

    ConnectionsCache.connectionCache.put("someKey", conn_a)
    val response: Connection = ConnectionsCache.connectionCache.getWithDefault("someKey", conn_b)
    assert(response == conn_a)
	}
}
