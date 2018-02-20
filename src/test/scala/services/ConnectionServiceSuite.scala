package services

import cache.TimedCache
import models.internal.{Connection, ConnectionGeneralInfo}
import org.scalatest.{BeforeAndAfter, FunSuite}
import roundrobin.models.api.{ConnectionResponse, EndpointWeight, WeightRate}
import utils.{ConfigurationServiceCreator, ConnectionCreator, ConnectionServiceCreator}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class ConnectionServiceSuite extends FunSuite
	with BeforeAndAfter
	with ConnectionCreator
	with ConfigurationServiceCreator
	with ConnectionServiceCreator {

	test("next endpoint - equal distribution") {
		val endpointPrefix: String = "endpoint"
		val endpointMap: Map[String, Int] = List(s"${endpointPrefix}_a", s"${endpointPrefix}_b", s"${endpointPrefix}_c", s"${endpointPrefix}_d").map(x=> x -> 100).toMap
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), endpointMap)
		val connectionCache = createConnectionCache(Map(connection.info.name -> connection))
		val connectionService: ConnectionService = getConnectionService(None, Some(connectionCache))

		val arrayBuffer: Array[Int] = new Array[Int](4)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size

		val connectionWeightList_1: String = connectionService.weightService.getConnectionWeight(connection) mkString ""

		(0 until rounds).foreach { _ =>
			connectionService.next(connection.info.name).right.foreach { endpoint =>
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

		val connectionWeightList_2: String = connectionService.weightService.getConnectionWeight(connection) mkString " "

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
		val cache = createWeightCache(
			Map("endpoint_a" -> EndpointWeight("endpoint_a", pointsService.getPoints(100)),
				"endpoint_b" -> EndpointWeight("endpoint_b", pointsService.getPoints(50)))
		)

		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val connectionCache = createConnectionCache(Map(connection.info.name -> connection))
		val connectionService: ConnectionService = getConnectionService(Some(cache), Some(connectionCache))


		val arrayBuffer: Array[Int] = new Array[Int](2)
		val rounds: Int = 40000
		val avg: Int = rounds / connection.endpoints.size
		(0 until rounds).foreach { _ =>
			connectionService.next(connection.info.name).right.foreach { endpoint =>
				val index: Int = if(endpoint.endpointName == "endpoint_a") 0
				else if(endpoint.endpointName == "endpoint_b") 1
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}

		assert(isWithinRatio(30000, arrayBuffer(0), 0.3), s"Avg: $avg, Count:${arrayBuffer(0)}")
	}

	test("next with single negative update") {
		val weightCache = createWeightCache(
			Map("endpoint_a" -> EndpointWeight("endpoint_a", pointsService.getPoints(100)),
					"endpoint_b" -> EndpointWeight("endpoint_b", pointsService.getPoints(50)))
		)
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val connectionCache = createConnectionCache(Map(connection.info.name -> connection))
		val connectionService: ConnectionService = getConnectionService(Some(weightCache), Some(connectionCache))

		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 10)
		connectionService.update(endpointName = "endpoint_a", weightRate) match {
			case Left(left) => fail(left)
			case Right(_) => connectionService.connectionWeight(connection.info.name) match {
				case Left(left) => fail(left)
				case Right(right) => assert(right.totalWeight == 140)
			}
		}
	}

	test("next with bad update params") {
		val weightCache = createWeightCache(
			Map("endpoint_a" -> EndpointWeight("endpoint_a", pointsService.getPoints(100)),
					"endpoint_b" -> EndpointWeight("endpoint_b", pointsService.getPoints(50)))
		)
		val connection: Connection = getConnection(ConnectionGeneralInfo("name"), Map("endpoint_a" -> 100, "endpoint_b" -> 50))
		val connectionCache = createConnectionCache(Map(connection.info.name -> connection))
		val connectionService: ConnectionService = getConnectionService(Some(weightCache), Some(connectionCache))

		val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 1000)
		connectionService.update(endpointName = "endpoint_a", weightRate).right.foreach { _ =>
			connectionService.connectionWeight(connection.info.name) match {
				case Left(left) => fail(left)
				case Right(right) => assert(right.totalWeight == 51)
			}
		}
	}

	test("next with connection_with_4_simple_endpoints which will be loaded from config file") {
		val connectionService: ConnectionService = getConnectionService()
		val arrayBuffer: Array[Int] = new Array[Int](4)
		val rounds: Int = 80000
		(0 until rounds).foreach { _ =>
			connectionService.next("connection_with_4_simple_endpoints").right.foreach { endpoint =>
				val index: Int = if(endpoint.value == "value_A") 0
				else if(endpoint.value == "value_B") 1
				else if(endpoint.value == "value_C") 2
				else if(endpoint.value == "value_D") 3
				else throw new Exception("")
				arrayBuffer.update(index, arrayBuffer(index)+1)
			}
		}
		arrayBuffer.foreach(count => assert(isWithinRatio(20000, count, 0.3), s"Avg: 20000, Count:$count"))
	}

	test("connection of connection") {
		val connectionService: ConnectionService = getConnectionService()
		connectionService.next("search_engines") match {
			case Left(left) => fail(left)
			case Right(response) =>
				assert(response.parentConnectionName == "search_engines")
				assert(response.connectionName == "search_google" || response.connectionName == "search_bing")
		}
	}

	test("80000 request - equal distribution for connection of connections") {
		val connectionService: ConnectionService = getConnectionService()
		val rounds: Int = 80000
		var arrayBuffer: List[ConnectionResponse] = List.empty

		(0 until rounds).foreach { _ =>
			connectionService.next("connection_with_endpoints_A_B").right.foreach { response =>
				arrayBuffer = List(response) ::: arrayBuffer
			}
		}
		arrayBuffer.groupBy(x=>x.endpointName).map { case (name, responses) =>
			name -> responses.size
		}.filterNot { case (_, count) =>
			isWithinRatio(20000, count, 0.2)
		}.foreach { case (name, count) => fail(s"$name $count") }
	}

	test("reduce endpoint") {
		val connectionService: ConnectionService = getConnectionService()
		(for {
			response <- connectionService.next("search_engines").right
			weight <- connectionService.update(response.endpointName, WeightRate(isSuccess = false, isPercent = false, 10)).right
		} yield weight) match {
			case Left(left) => fail(left)
			case Right(updatedWeight) =>
				assert(updatedWeight.size == 90)
				connectionService.getConnection("search_engines") match {
					case Left(left) => fail(left)
					case Right(connection) => connectionService.connectionWeight(connection.info.name) match {
						case Left(left) => fail(left)
						case Right(connectionWeight) => assert(connectionWeight.totalWeight == 290)
					}
				}
		}
	}

	test("reduce google to 50 and -> 'search_engines' total weight has to be 150") {
		val connectionService: ConnectionService = getConnectionService()
		(0 until 5).foreach { _ => connectionService.update("google_com", WeightRate(isSuccess = false, isPercent = false, quantity = 10)) }
		connectionService.connectionWeight("search_engines") match {
			case Left(left) => fail(left)
			case Right(result) => assert(result.totalWeight == 250)
		}
	}

	test("connection with 4 simple points async next 40000 times equal distribution") {
		val connectionService: ConnectionService = getConnectionService()
		val arrayBuffer: Array[Int] = new Array[Int](4)
		val rounds: Int = 40000
		val avg: Int = rounds / arrayBuffer.length
		(0 until 40000).foreach { _ =>
			connectionService.nextAsync("connection_with_4_simple_endpoints").onComplete {
				case Success(result) => result match {
					case Left(left) => fail(left)
					case Right(res) =>
						val index = res.endpointName.toLowerCase match {
							case "endpoint_a" => 0
							case "endpoint_b" => 1
							case "endpoint_c" => 2
							case "endpoint_d" => 3
						}
						arrayBuffer.update(index, arrayBuffer(index)+1)
				}
				case Failure(failureMessage) => fail(failureMessage.getMessage)
			}
		}

		Thread.sleep(10000)
		arrayBuffer.toList.indices.map { id =>
			val count = arrayBuffer(id)
			assert(isWithinRatio(avg, count, 0.3),
				s"Avg: $avg, \n" + s"${arrayBuffer.toList.indices.map(x=>x -> arrayBuffer(x)).toMap}")
		}
	}

	test("connection with 4 simple endpoints when updating the first endpoint in an async way") {
		val connectionService: ConnectionService = getConnectionService()
		val connection: Connection = connectionService.getConnection("connection_with_4_simple_endpoints").right.get
		val endpoint: String = connection.endpointNames.head
		connectionService.updateAsync(endpoint, WeightRate(isPercent = false, isSuccess = false, quantity = 10)).onComplete {
			case Success(_) => connectionService.connectionWeight("connection_with_4_simple_endpoints") match {
				case Left(left) => fail(left)
				case Right(result) => assert(result.totalWeight == 390)
			}
			case Failure(f) => fail(f.getMessage)
		}
		connectionService.connectionWeight("connection_with_4_simple_endpoints") match {
			case Left(left) => fail(left)
			case Right(result) => assert(result.totalWeight == 400 || result.totalWeight == 390)
		}

		Thread.sleep(2000)
	}

	test("load inactive connection") {
		val connectionService: ConnectionService = getConnectionService()
		connectionService.load("inactive_connection") match {
			case Left(error) => assert(error == "inactive_connection is deactivated")
			case Right(_) => fail("loaded inactive_connection")
		}
	}

	test("load connection_with_metadata") {
		getConnectionService().load("connection_with_metadata") match {
			case Left(error) => fail(error)
			case Right(connection) =>
				assert(connection.metadata.hasMetadata)
				assert(connection.metadata.list.lengthCompare(1) == 0)
				assert(connection.metadata.list.head.key == "key")
		}
	}

	private def isWithinRatio(avg: Int, amount: Int, delta: Double): Boolean = {
		val min: Int = (avg * (1 - delta)).toInt
		val max: Int = (avg * (1 + delta)).toInt
		amount > min && amount < max
	}

	private def createWeightCache(nameToWeight: Map[String, EndpointWeight]): TimedCache[String, EndpointWeight] = {
		val cache = TimedCache.apply[String, EndpointWeight]()
		nameToWeight.foreach { case (name, weight) => cache.put(name, weight)}
		cache
	}

	private def createConnectionCache(nameToConnection: Map[String, Connection]): TimedCache[String, Connection] = {
		val cache = TimedCache.apply[String, Connection]()
		nameToConnection.foreach { case (name, connection) => cache.put(name, connection)}
		cache
	}
}