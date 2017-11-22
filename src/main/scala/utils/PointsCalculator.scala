package utils

import models.{Weight, Point}

object PointsCalculator {

	def minDistance(point: Point, groups: List[Weight]): Weight = {
		if(groups.size == 1)  groups.last
		else {
			val currentGroup: Weight = groups.head
			val nextResult: Weight = minDistance(point, groups.tail)
			if(currentGroup - point < nextResult - point) currentGroup else nextResult
		}
	}
}
