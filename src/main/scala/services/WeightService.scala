package services

import models.{Connection, Point, Weight, WeightRate}
import repositories.WeightRepository
import utils.{Consts, PointsCalculator}

class WeightService(val pointsService: PointsService,
										val weightRepository: WeightRepository){

	def create(name: String): Weight = Weight(name, pointsService.getPoints(Consts.POINTS_MAX_AMOUNT))
	def updateWeight(weight: Weight, weightRate: WeightRate): Either[String, Weight] = {
		val newWeight: Weight = weight.copy(points = pointsService.update(weight.points,
																																			weightRate.isSuccess,
																																			weightRate.isPercent,
																																			weightRate.quantity))
    weightRepository.updateWeight(newWeight.endPointName, newWeight)
	}

	def updateWeight(weightName: String, weightRate: WeightRate): Either[String, Weight] = {
		val weight: Either[String, Weight] = weightRepository.get(weightName) match {
			case Left(_) => weightRepository.updateWeight(weightName, create(weightName))
			case Right(w) => Right(w)
		}

		weight.right.flatMap { weight =>
			updateWeight(weight, weightRate)
		}
  }

	def next(groups: List[Weight]): Weight = nextImpl(groups, pointsService.getPoint)
//	def nextConnection(connections: List[Connection]): Either[String, (Connection, Weight)] = {
//		val point: Point = pointsService.getPoint
//		val connectionToWeight: List[(Connection, Either[String, Weight])] = getConnections(connections).map(connection => connection -> getConnectionWeights(connection).right.flatMap { weights =>
//			Right(nextImpl(weights, point))
//		})
//
//		if(connectionToWeight.exists(x=>x._2.isLeft))	Left(connectionToWeight.filter(x=>x._2.isLeft).map(x=>x._2.left.get) mkString "")
//		else 																					Right(findMin(connectionToWeight.map { case (connection, weight) => connection -> weight.right.get }, point))
//
//	}

//	private def getConnections(connections: List[Connection]): List[Connection] = {
//		connections
//	}


	def getOrDefault(name: String, default: Option[Weight] = None): Either[String, Weight] = {
		weightRepository.get(name, default)
	}

	private def nextImpl(groups: List[Weight], point: Point): Weight = {
		PointsCalculator.minDistance(point, groups)
	}

//	private def getConnectionWeights(connection: Connection): Either[String, List[Weight]] = {
//		val connectionWeghts: List[Either[String, Weight]] = connection.endpointsList.map(endpoint=>weightRepository.get(endpoint.name, Some(create(endpoint.name))))
//		val errorToWeight: (List[String], List[Weight]) = connectionWeghts.foldRight((List.empty[String], List.empty[Weight])) {
//			case (collector, (leftResult, rightResult)) =>
//				collector.fold(left => (left :: leftResult, rightResult), right => (leftResult, right :: rightResult))
//		}
//		if(errorToWeight._1.nonEmpty) Left(errorToWeight._1 mkString "\n")
//		else													Right(errorToWeight._2)
//	}
//
//	private def findMin(connWeight: List[(Connection, Weight)], point: Point): (Connection, Weight) = {
//		if(connWeight.size == 1)	connWeight.head
//		else	{
//			val current: (Connection, Weight) = connWeight.head
//			val next: (Connection, Weight) = findMin(connWeight.tail, point)
//			if(current._2-point < next._2-point)	current	else	next
//		}
//	}
}
