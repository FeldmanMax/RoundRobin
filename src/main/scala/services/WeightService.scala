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
		(weightRepository.get(weightName) match {
			case Left(_) => weightRepository.updateWeight(weightName, create(weightName))
			case Right(w) => Right(w)
		}).right.flatMap { weight =>
			updateWeight(weight, weightRate)
		}
  }

	def next(groups: List[Weight]): Weight = nextImpl(groups, pointsService.getPoint)
	def getOrDefault(name: String, default: Option[Weight] = None): Either[String, Weight] = weightRepository.get(name, default)
	def getConnectionWeight(connection: Connection): List[Weight] = {
		connection.endpointNames.map { endpointName => getOrDefault(endpointName, Option(create(endpointName)))
		}.filter(x=>x.isRight).map(x=>x.right.get)
	}

	private def nextImpl(groups: List[Weight], point: Point): Weight = PointsCalculator.minDistance(point, groups)
}
