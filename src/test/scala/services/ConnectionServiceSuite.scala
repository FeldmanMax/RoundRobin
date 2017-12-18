package services

import cache.TimedCache
import models.{Connection, ConnectionGeneralInfo, Weight, WeightRate}
import org.scalatest.{BeforeAndAfter, FunSuite}
import utils.{ConfigurationServiceCreator, ConnectionCreator, ConnectionServiceCreator}
import scala.concurrent.ExecutionContext.Implicits.global

class ConnectionServiceSuite extends FunSuite
	with BeforeAndAfter
	with ConnectionCreator
	with ConfigurationServiceCreator
	with ConnectionServiceCreator {

	test("next endpoint - equal distribution") {
		val endpointPrefix: String = "endpoint"
		val endpoint_a = s"${endpointPrefix}_a"
		val endpoint_b = s"${endpointPrefix}_b"
		val endpoint_c = s"${endpointPrefix}_c"
		val endpoint_d = s"${endpointPrefix}_d"
		val connectionService = getConnectionService()
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map(endpoint_a -> 100, endpoint_b -> 100, endpoint_c -> 100, endpoint_d -> 100))

		val arrayBuffer: Array[Int] = new Array[Int](4)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size

		val connectionWeightList_1: String = connectionService.connectionWeightList(connection) mkString ""

		(0 until rounds).foreach { _ =>
			connectionService.next(connection).right.foreach { endpoint =>
				val index: Int = endpoint.endpointName match {
					case "endpoint_a" => 0
					case "endpoint_b" => 1
					case "endpoint_c" => 2
					case "endpoint_d" => 3
					case _ => throw new Exception("")
				}
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}

		val connectionWeightList_2: String = connectionService.connectionWeightList(connection) mkString " "

		arrayBuffer.toList.indices.map { id =>
			val count = arrayBuffer(id)
			assert(isWithinRatio(avg, count, 0.3),
				s"Avg: $avg, \n" +
					s"$connectionWeightList_1 \n" +
					s"$connectionWeightList_2 \n" +
					s"${arrayBuffer.toList.indices.map(x=>x -> arrayBuffer(x)).toMap}")
		}
	}

	test("next endpoint - first will have twice as much") {
		val cache = createCache(
			Map("endpoint_a" -> Weight("endpoint_a", pointsService.getPoints(100)),
				"endpoint_b" -> Weight("endpoint_b", pointsService.getPoints(50)))
		)
		val connectionService: ConnectionService = getConnectionService(Some(cache))
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
		val cache = createCache(
			Map("endpoint_a" -> Weight("endpoint_a", pointsService.getPoints(100)),
					"endpoint_b" -> Weight("endpoint_b", pointsService.getPoints(50)))
		)

		val connectionService: ConnectionService = getConnectionService(Some(cache))
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 10)
		connectionService.update(endpointName = "endpoint_a", weightRate).right.foreach { result =>
			assert(connectionService.getWeight(connection) == 140)
		}
	}

	test("next with bad update params") {
		val cache = createCache(
			Map("endpoint_a" -> Weight("endpoint_a", pointsService.getPoints(100)),
					"endpoint_b" -> Weight("endpoint_b", pointsService.getPoints(50)))
		)
		val connectionService: ConnectionService = getConnectionService(Some(cache))
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 1000)
		connectionService.update(endpointName = "endpoint_a", weightRate).right.foreach { result =>
			assert(connectionService.getWeight(connection) == 51)
		}
	}

	test("next with connection_1 which will be loaded from config file") {
		val connectionService: ConnectionService = getConnectionService()
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

	private def createCache(nameToWeight: Map[String, Weight]): TimedCache[String, Weight] = {
		val cache = TimedCache.apply[String, Weight]()
		nameToWeight.foreach { case (name, weight) => cache.put(name, weight)}
		cache
	}
}