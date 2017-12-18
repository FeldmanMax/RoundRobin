package services

import models.{Weight, WeightRate}
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
    weightRepository.updateWeight(newWeight.name, newWeight)
	}
	def updateWeight(weightName: String, weightRate: WeightRate): Either[String, Weight] = {
		val weight: Either[String, Weight] = weightRepository.get(weightName) match {
			case Left(_) => weightRepository.updateWeight(weightName, create(weightName))
			case Right(weight) => Right(weight)
		}

		weight.right.flatMap { weight =>
			updateWeight(weight, weightRate)
		}
  }
	def next(groups: List[Weight]): Weight = PointsCalculator.minDistance(pointsService.getPoint, groups)
	def getOrDefault(name: String, default: Option[Weight] = None): Either[String, Weight] = {
		weightRepository.get(name, default)
	}
}
