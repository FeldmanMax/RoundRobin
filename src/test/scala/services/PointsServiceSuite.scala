package services

import models.{EndpointWeight, Point}
import org.scalatest.{BeforeAndAfter, FunSuite}
import utils.{WeightCreator, PointsCreator}

class PointsServiceSuite extends FunSuite with BeforeAndAfter with PointsCreator with WeightCreator {

	var testPoint: Point = _

	before {
		testPoint = Point(1, 0)
	}

	test("1. 100 points radius equals 1") {
		val points: List[Point] = getPoints(100)
		val middlePoint: Point = Point(0, 0)
		points.foreach(x=>assert(x - middlePoint == 1))
	}

	test("2. min of 2 points") {
		val group: EndpointWeight = EndpointWeight("name", getPoints(2))
		val minDistnace: Double = group - testPoint
		val expected: Double = group.points.sortBy(point=>point.x).last - testPoint
		assert(minDistnace - expected == 0.0)
	}

	test("3. min of 2 groups") {
		val groupAPoints: List[Point] = getPointsWithMaxX(0.34, 10)
		val groupBPoints: List[Point] = getPointsWithMaxX(0.35, 10)

		val groupA: EndpointWeight = getWeight(groupAPoints, "groupA", 0)
		val groupB: EndpointWeight = getWeight(groupBPoints, "groupB", 0)
		assert((groupA - testPoint) > (groupB - testPoint))
	}

	test("4. update points: isPercent=true -> 30%, increase=true, points: 100 => expected same points") {
		val points: List[Point] = getPoints(100)
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(points, isIncrease = true, isPercent = true, 30)
		assert(points == result)
	}

	test("4. update points: isPercent=false -> 20, increase=false, points: 22 => 2 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(22), isIncrease = false, isPercent = false, 20)
		assert(result.length == 2)
	}

	test("4. update points: isPercent=false -> 20, increase=false, points: 21 => 1 point") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(21), isIncrease = false, isPercent = false, 20)
		assert(result.length == 1)
	}

	test("4. update points: isPercent=false -> 20, increase=false, points: 20 => 1 point") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(20), isIncrease = false, isPercent = false, 20)
		assert(result.length == 1)
	}

	test("4. update points: isPercent=true -> 80, increase=false, points: 1 => 1 point") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(1), isIncrease = false, isPercent = true, 80)
		assert(result.length == 1)
	}

	test("4. update points: isPercent=true -> 80, increase=true, points: 1 => 2 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(1), isIncrease = true, isPercent = true, 80)
		assert(result.length == 2)
	}

	test("4. update points: isPercent=false -> 30, increase=true, points: 69 => 99 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(69), isIncrease = true, isPercent = false, 30)
		assert(result.length == 99)
	}

	test("4. update points: isPercent=false -> 31, increase=true, points: 69 => 100 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(69), isIncrease = true, isPercent = false, 31)
		assert(result.length == 100)
	}

	test("4. update points: isPercent=false -> 32, increase=true, points: 69 => 100 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(69), isIncrease = true, isPercent = false, 32)
		assert(result.length == 100)
	}

	test("4. update points: isPercent=true -> 50, increase=true, points: 69 => 100 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(69), isIncrease = true, isPercent = true, 50)
		assert(result.length == 100)
	}

	test("4. update points: isPercent=true -> 50, increase=true, points: 50 => 75 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(50), isIncrease = true, isPercent = true, 50)
		assert(result.length == 75)
	}

	test("4. update points: isPercent=true -> 50, increase=false, points: 50 => 25 points") {
		val service: PointsService = new PointsService()
		val result: List[Point] = service.update(getPoints(50), isIncrease = false, isPercent = true, 50)
		assert(result.length == 25)
	}
}
