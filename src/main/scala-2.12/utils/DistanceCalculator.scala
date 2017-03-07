package utils

import data_modules.{Angle, Endpoint, Endpoints}
import modules.Connection

object DistanceCalculator {

	def getClosestEndpoint(endpoints: List[Endpoint], angle: Angle): Endpoint = {
		val endpointsToDistance: Map[Endpoint, Double] = endpoints.map(x=> (x, getClosestDistance(x, angle))).toMap
		endpointsToDistance.minBy(x=> Math.min(x._2, angle.angle))._1
	}

	def getClosestEndpoint(endpoints: Endpoints, angle: Angle): Endpoint = {
		getClosestEndpoint(endpoints.endpoints, angle)
	}

	def getClosestConnection(connections: Seq[Connection], angle: Angle): Connection = {
		val connectionsToDistance: Map[Connection, Double] = connections.map(x=> (x, getClosestDistance(x, angle))).toMap
		connectionsToDistance.minBy(x=> Math.min(x._2, angle.angle))._1
	}

  /*
  * Returns a distance between the 2 angles.
  * */
  private def getDistance(angleA: Angle, angleB: Angle): Double = {
		math.min(math.abs(angleA.angle - angleB.angle), math.abs(angleA.angle - angleB.completeAngle))
  }

  private def singleAngleComparison(angleA: (Double, Angle), angleB: (Double, Angle)): (Double, Angle) = {
		if(angleA._1 < angleB._1) angleA else angleB
  }

	private def getClosestDistance(endpoint: Endpoint, angle: Angle): Double = {
		val distanceToAngleInstance: List[(Double, Angle)] = endpoint.points.map(x=>(getDistance(x, angle), x))
		distanceToAngleInstance.reduceLeft(singleAngleComparison)._1
	}

	private def getClosestDistance(endpoint: Connection, angle: Angle): Double = {
		val distanceToAngleInstance: List[(Double, Angle)] = endpoint.connectionAngles.map(x=>(getDistance(x, angle), x))
		distanceToAngleInstance.reduceLeft(singleAngleComparison)._1
	}
}
