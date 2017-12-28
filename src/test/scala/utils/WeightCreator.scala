package utils

import cache.ConnectionsCache
import models.{Point, EndpointWeight}
import services.PointsService

trait WeightCreator {
	val pointsService: PointsService = new PointsService()

	def getWeight(point: Point, groupName: String, amount: Int): EndpointWeight = getWeight(List(point), groupName, amount)
	def getWeight(points: List[Point], groupName: String, amount: Int): EndpointWeight = {
		EndpointWeight(groupName, pointsService.getPoints(amount) ::: points)
	}
	def getWeight(name: String, quantity: Int): EndpointWeight = {
		val weight: EndpointWeight = EndpointWeight(name, pointsService.getPoints(quantity))
		ConnectionsCache.weightCache.put(name, weight)
		weight
	}
}
