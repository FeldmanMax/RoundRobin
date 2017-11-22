package services

import models.{Connection, ConnectionGeneralInfo, WeightRate}
import org.scalatest.{BeforeAndAfter, FunSuite}
import utils.ConnectionCreator

class ConnectionServiceSuite extends FunSuite with BeforeAndAfter with ConnectionCreator {
	private var connectioService: ConnectionService = _

	before {
		connectioService = new ConnectionService(new WeightService(new PointsService()))
	}

	test("1. next endpoint - equal distribution") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 100, "endpoint_c" -> 100, "endpoint_d" -> 100))

		val arrayBuffer: Array[Int] = new Array[Int](4)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size
		(0 until rounds).foreach { _ =>
			connectioService.next(connection).right.foreach { endpoint =>
				val index: Int = if(endpoint.name == "endpoint_a") 0
				else if(endpoint.name == "endpoint_b") 1
				else if(endpoint.name == "endpoint_c") 2
				else if(endpoint.name == "endpoint_d") 3
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}

		arrayBuffer.foreach(count => assert(isWithinRatio(avg, count, 0.3), s"Avg: $avg, Count:$count"))
	}

	test("2. next endpoint - first will have twice as much") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val arrayBuffer: Array[Int] = new Array[Int](2)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size
		(0 until rounds).foreach { _ =>
			connectioService.next(connection).right.foreach { endpoint =>
				val index: Int = if(endpoint.name == "endpoint_a") 0
				else if(endpoint.name == "endpoint_b") 1
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}

		assert(isWithinRatio(30000, arrayBuffer(0), 0.3), s"Avg: $avg, Count:${arrayBuffer(0)}")
	}

	test("3. next with single negative update") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 10)
		connectioService.update(connection, endpointName = "endpoints_a", weightRate).right.foreach { result =>
			assert(result.totalWeight == 140)
		}
	}

	test("4. next with bad update params") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 1000)
		connectioService.update(connection, endpointName = "endpoints_a", weightRate).right.foreach { result =>
			assert(result.totalWeight == 51)
		}
	}

	private def isWithinRatio(avg: Int, amount: Int, delta: Double): Boolean = {
		val min: Int = (avg * (1 - delta)).toInt
		val max: Int = (avg * (1 + delta)).toInt
		amount > min && amount < max
	}
}
