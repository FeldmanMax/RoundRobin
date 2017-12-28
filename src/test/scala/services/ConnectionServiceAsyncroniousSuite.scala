package services

import models.{Connection, ConnectionGeneralInfo, ConnectionWeight, WeightRate}
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfter, FunSuite}
import repositories.{ConnectionRepository, WeightRepository}
import utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class ConnectionServiceAsyncroniousSuite extends FunSuite
  with BeforeAndAfter
  with ConnectionCreator
  with ConfigurationServiceCreator
  with ConnectionServiceCreator {

  test("scenario a - many readers and one updater") {
    val connection: Connection = getConnection(ConnectionGeneralInfo("name", false), Map("endpoint_a" -> 100))
    val rounds: Int = 100000

    val connectionService: ConnectionService = getConnectionService()
    val future: Future[List[List[(DateTime, String)]]] = Future.sequence(
      List(getWeightReadFuture(rounds, connection, connectionService),
        getWeightReadFuture(rounds, connection, connectionService),
        getWeightReadFuture(rounds, connection, connectionService), updateConnectionFuture(100, sleepTimeMillis = 10, connectionService)))
    val lists: List[List[(DateTime, String)]] = Await.result[List[List[(DateTime, String)]]](future, Duration.Inf)
    val sorted: List[(DateTime, String)] = lists.flatten.sortBy(x=>x._1)(Implicits.JodaOrdering.dateTimeOrdering)
    val groupedByString: Map[String, List[(DateTime, String)]] = sorted.groupBy(x=>x._2)
    val stringToAmount: Map[String, Int] = groupedByString.map(x=>x._1 -> x._2.size)
    val dsds = 10
  }

  private def getWeightReadFuture(rounds: Int, connection: Connection, connectionService: ConnectionService): Future[List[(DateTime, String)]] = {
    Future {
      (0 until rounds).map { _ =>
        val connectionWeight: String = connectionService.connectionWeight(connection.info.name) match {
          case Left(left) => left
          case Right(right) => right.totalWeight + ""
        }

        DateTime.now() -> s"Thread: ${Thread.currentThread().getName} totalWeight: $connectionWeight"
      }.toList
    }
  }

  private def updateConnectionFuture(rounds: Int, sleepTimeMillis: Int, connectionService: ConnectionService): Future[List[(DateTime, String)]] = {
    Future {
      (0 until rounds).map { _ =>
        val weightRate: WeightRate = WeightRate(isSuccess = false, isPercent = false, 5)
        connectionService.update("endpoint_a", weightRate)
        val retValue = DateTime.now() -> s"Thread: ${Thread.currentThread().getName} weightRate: ${weightRate.toString}"
        Thread.sleep(sleepTimeMillis)
        retValue
      }.toList
    }
  }
}
