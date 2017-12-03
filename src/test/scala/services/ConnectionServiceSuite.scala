package services

import models.{Connection, ConnectionGeneralInfo, WeightRate}
import org.scalatest.{BeforeAndAfter, FunSuite}
import repositories.{ConnectionRepository, WeightRepository}
import utils.{ConfigurationServiceCreator, ConnectionCreator}

class ConnectionServiceSuite extends FunSuite with BeforeAndAfter with ConnectionCreator  with ConfigurationServiceCreator {
	private var connectionService: ConnectionService = _
	before {
		connectionService = new ConnectionService(new WeightService(new PointsService(), new WeightRepository()), new ConnectionRepository(),
			configServiceWithFileConfiguration())
	}

	test("next endpoint - equal distribution") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 100, "endpoint_c" -> 100, "endpoint_d" -> 100))

		val arrayBuffer: Array[Int] = new Array[Int](4)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size
		(0 until rounds).foreach { _ =>
			connectionService.next(connection).right.foreach { endpoint =>
				val index: Int = if(endpoint.endpointName == "endpoint_a") 0
				else if(endpoint.endpointName == "endpoint_b") 1
				else if(endpoint.endpointName == "endpoint_c") 2
				else if(endpoint.endpointName == "endpoint_d") 3
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}

		arrayBuffer.foreach(count => assert(isWithinRatio(avg, count, 0.3), s"Avg: $avg, Count:$count"))
	}

	test("next endpoint - first will have twice as much") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val arrayBuffer: Array[Int] = new Array[Int](2)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size
		(0 until rounds).foreach { _ =>
			connectionService.next(connection).right.foreach { endpoint =>
				val index: Int = if(endpoint.endpointName == "endpoint_a") 0
				else if(endpoint.endpointName == "endpoint_b") 1
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}

		assert(isWithinRatio(30000, arrayBuffer(0), 0.3), s"Avg: $avg, Count:${arrayBuffer(0)}")
	}

	test("next with single negative update") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 10)
		connectionService.update(endpointName = "endpoint_a", weightRate).right.foreach { result =>
			assert(connectionService.getWeight(connection) == 140)
		}
	}

	test("next with bad update params") {
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 1000)
		connectionService.update(endpointName = "endpoint_a", weightRate).right.foreach { result =>
			assert(connectionService.getWeight(connection) == 51)
		}
	}

	test("next with connection_1 which will be loaded from config file") {
		val arrayBuffer: Array[Int] = new Array[Int](2)
		val rounds: Int = 40000
		(0 until rounds).foreach { _ =>
			connectionService.next("connection_1").right.foreach { endpoint =>
				val index: Int = if(endpoint.value == "value_1_1") 0
				else if(endpoint.value == "value_1_2") 1
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}
		arrayBuffer.foreach(count => assert(isWithinRatio(20000, count, 0.3), s"Avg: 20000, Count:$count"))
	}

	private def isWithinRatio(avg: Int, amount: Int, delta: Double): Boolean = {
		val min: Int = (avg * (1 - delta)).toInt
		val max: Int = (avg * (1 + delta)).toInt
		amount > min && amount < max
	}
}