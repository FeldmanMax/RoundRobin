package services

import com.google.inject.Inject
import models.internal.Point
import utils.{Consts, PointGenerator}

class PointsService @Inject()(){

	def getPoint: Point = PointGenerator.next()
	def getPoints(quantity: Int): List[Point] = {
		(0 until quantity).map(_ => PointGenerator.next()).toList
	}

	def update(points: List[Point], isIncrease: Boolean, isPercent: Boolean, quantity: Int): List[Point] = {
		getCalculatedQuantity(points.length, isIncrease, isPercent, quantity) match {
			case amount if amount == 0  => points
			case amount if amount > 0   => points ::: getPoints(amount)
			case amount if amount < 0   => points.drop(-amount)
		}
	}

	private def getCalculatedQuantity(totalPoints: Int, isIncrease: Boolean, isPercent: Boolean, quantity: Int): Int = {
		val absDeltaAmount: Int = if (isPercent) Math.ceil(totalPoints * (100.0 - quantity) / 100).toInt else quantity
		val deltaAmount: Int = if(isIncrease) absDeltaAmount else -absDeltaAmount
		val calculatedPointsAmount: Int = totalPoints + deltaAmount
		val retValue = if(calculatedPointsAmount >= Consts.POINTS_MIN_AMOUNT && calculatedPointsAmount <= Consts.POINTS_MAX_AMOUNT) deltaAmount
		else if(calculatedPointsAmount > Consts.POINTS_MAX_AMOUNT)  Consts.POINTS_MAX_AMOUNT - totalPoints
		else if(calculatedPointsAmount < Consts.POINTS_MIN_AMOUNT)  -(totalPoints - Consts.POINTS_MIN_AMOUNT)
		else deltaAmount
		retValue
	}

}
