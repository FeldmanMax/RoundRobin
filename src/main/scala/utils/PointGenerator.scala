package utils

import models.internal.Point

import scala.util.Random
import utils.RoundRobinImplicits.DoubleExtension

object PointGenerator {

	private val randomX: Random = Random
	private val randomY: Random = Random

	def next(): Point = {
		val x: Double = defineX()
		Point(x, defineY(x, randomY.nextBoolean()))
	}

	def next(definedX: Double, isPositiveY: Boolean = true): Point = Point(definedX, defineY(definedX, isPositiveY))

	private def defineX(): Double = {
		val randomValue: Double = randomX.nextDouble()
		if(randomValue.isEqual(0.5, Consts.EPSILON)) 0.0
		else  if(randomValue > 0.5) (randomValue - 0.5) * 2
		else                        -((randomValue - 0.5) * 2)
	}

	private def defineY(x: Double, isPositive: Boolean): Double = {
		val isPositive: Boolean = randomY.nextBoolean()
		val y = Math.sqrt(1 - Math.pow(x, 2))
		if(isPositive)  y else  -y
	}
}
