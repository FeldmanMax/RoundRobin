package utils

import cache.ConnectionsCache
import models.{Point, Weight}
import services.PointsService

trait WeightCreator {
	val pointsService: PointsService = new PointsService()

	def getWeight(point: Point, groupName: String, amount: Int): Weight = getWeight(List(point), groupName, amount)
	def getWeight(points: List[Point], groupName: String, amount: Int): Weight = {
		Weight(groupName, pointsService.getPoints(amount) ::: points)
	}
	def getWeight(name: String, quantity: Int): Weight = {
		val weight: Weight = Weight(name, pointsService.getPoints(quantity))
		ConnectionsCache.weightCache.put(name, weight)
		weight
	}
}
