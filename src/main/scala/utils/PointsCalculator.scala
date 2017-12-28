package utils

import models.{EndpointWeight, Point}

object PointsCalculator {

	def minDistance(point: Point, groups: List[EndpointWeight]): EndpointWeight = {
		if(groups.size == 1)  groups.last
		else {
			val currentGroup: EndpointWeight = groups.head
			val nextResult: EndpointWeight = minDistance(point, groups.tail)
			if(currentGroup - point < nextResult - point) currentGroup else nextResult
		}
	}
}
