package utils

import models.internal.Point
import services.PointsService

trait PointsCreator {
	def getPointsWithMaxX(maxX: Double, amount: Int): List[Point] = {
		val service: PointsService = new PointsService()
		val points: List[Point] = service.getPoints(amount*10)
		points.filter(point => point.x < maxX).take(amount-1) ::: List(PointGenerator.next(maxX))
	}

	def getPointsWithMinX(minX: Double, amount: Int): List[Point] = {
		val service: PointsService = new PointsService()
		val points: List[Point] = service.getPoints(amount*10)
		points.filter(point => point.x > minX).take(amount-1) ::: List(PointGenerator.next(minX))
	}

	def getPoints(amount: Int): List[Point] = {
		val service: PointsService = new PointsService()
		service.getPoints(amount)
	}
}
