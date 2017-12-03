package services

import cache.ConnectionsCache
import models.{Weight, WeightRate}
import repositories.WeightRepository
import utils.{Consts, PointsCalculator}

class WeightService(val pointsService: PointsService,
										val weightRepository: WeightRepository){

	def create(name: String): Weight = Weight(name, pointsService.getPoints(Consts.POINTS_MAX_AMOUNT))
	def updateWeight(weight: Weight, weightRate: WeightRate): Weight = {
		val newWeight: Weight = weight.copy(points = pointsService.update(weight.points,
																							weightRate.isSuccess,
																							weightRate.isPercent,
																							weightRate.quantity))
    ConnectionsCache.weightCache.put(newWeight.name, newWeight)
    newWeight
	}
	def updateWeight(weightName: String, weightRate: WeightRate): Either[String, Weight] = {
    ConnectionsCache.weightCache.getOrAdd(weightName, create(weightName)).flatMap { weight =>
      Right(updateWeight(weight, weightRate))
    }
  }
	def next(groups: List[Weight]): Weight = PointsCalculator.minDistance(pointsService.getPoint, groups)
	def getOrDefault(name: String, default: Option[Weight] = None): Either[String, Weight] = {
		weightRepository.get(name, default)
	}
}
