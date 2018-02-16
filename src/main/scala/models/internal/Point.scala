package models.internal

import utils.Consts
case class Point(x: Double, y: Double) {
	def - (other: Point): Double = {
		val dist = Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2))
		if(Math.ceil(dist) - dist < Consts.EPSILON) Math.ceil(dist) else dist
	}

	override def toString: String = s"(x:$x, y:$y)"
}