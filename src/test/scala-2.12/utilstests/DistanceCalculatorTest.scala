package utilstests

import data_modules.{Angle, Endpoint}
import org.scalatest.FunSuite
import utils.DistanceCalculator

import scala.util.Random

class DistanceCalculatorTest extends FunSuite {
	test("random point multiple endpoints 1") {
		val pointsRanges_1 = List[Angle](Angle(0.4), Angle(0.5), Angle(0.6), Angle(0.7))
		val pointsRanges_2 = List[Angle](Angle(0.4), Angle(0.55), Angle(0.66), Angle(0.77))
		val randomAngle = Angle(0.62)

		val endpoint_1 = Endpoint("name_1", "value_1", pointsRanges_2)
		val endpoint_2 = Endpoint("name_2", "value_2", pointsRanges_2)

		val result: Endpoint = DistanceCalculator.getClosestEndpoint(List[Endpoint](endpoint_1, endpoint_2), randomAngle)
		assert(result.name == endpoint_1.name)
	}

	test("random point multiple endpoints 2") {
		val pointsRanges_1 = List[Angle](Angle(0.4), Angle(0.5), Angle(0.6), Angle(0.7))
		val pointsRanges_2 = List[Angle](Angle(0.4), Angle(0.55), Angle(0.66), Angle(0.77))
		val randomAngle = Angle(0.54)

		val endpoint_1 = Endpoint("name_1", "value_1", pointsRanges_1)
		val endpoint_2 = Endpoint("name_2", "value_2", pointsRanges_2)

		val result: Endpoint = DistanceCalculator.getClosestEndpoint(List[Endpoint](endpoint_1, endpoint_2), randomAngle)
		assert(result.name == endpoint_2.name)
	}

	private def getNextAngle: Double = {
		val random: Random = new Random()
		val randomAngle: Double = random.nextDouble() * math.Pi
		randomAngle
	}
}
