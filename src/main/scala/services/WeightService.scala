package services

import models.{Weight, WeightRate}
import utils.{Consts, PointsCalculator}

class WeightService(val pointsService: PointsService){

	def create(name: String): Weight = Weight(name, pointsService.getPoints(Consts.POINTS_MAX_AMOUNT))
	def updateWeight(weight: Weight, weightRate: WeightRate): Weight = {
		weight.copy(points = pointsService.update(weight.points,
																							weightRate.isSuccess,
																							weightRate.isPercent,
																							weightRate.quantity))
	}
	def next(groups: List[Weight]): Weight = PointsCalculator.minDistance(pointsService.getPoint, groups)
}
