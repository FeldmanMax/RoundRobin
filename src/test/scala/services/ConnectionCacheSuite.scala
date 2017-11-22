package services

import cache.ConnectionsCache
import org.scalatest.FunSuite
import utils.ConnectionCreator

class ConnectionCacheSuite extends FunSuite with ConnectionCreator {
	test("add to cache") {
    val serializabled: String = getSerializedConnection()
    ConnectionsCache.connectionCache.put("someKey", serializabled)
    val response: String = ConnectionsCache.connectionCache.getWithDefault("someKey", "bad data")
    assert(response != "bad data")
	}
}
