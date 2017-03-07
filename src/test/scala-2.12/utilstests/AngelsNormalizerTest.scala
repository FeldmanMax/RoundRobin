package utilstests

import configuration.ConnectionLimitations
import org.scalatest.FunSuite
import utils.AnglesNormalizer

class AngelsNormalizerTest extends FunSuite {
	test("overallAngles: 50, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 25") {
		assert(AnglesNormalizer.normalize(50, 1, 100, 2) == 25)
	}

	test("overallAngles: 100, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 50") {
		assert(AnglesNormalizer.normalize(100, 1, 100, 2) == 50)
	}

	test("overallAngles: 150, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 75") {
		assert(AnglesNormalizer.normalize(150, 1, 100, 2) == 75)
	}

	test("overallAngles: 200, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 100") {
		assert(AnglesNormalizer.normalize(200, 1, 100, 2) == 100)
	}

	test("overallAngles: 250, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 100") {
		assert(AnglesNormalizer.normalize(250, 1, 100, 2) == 100)
	}

	test("overallAngles: 10, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 5") {
		assert(AnglesNormalizer.normalize(10, 1, 100, 2) == 5)
	}

	test("overallAngles: 2, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 2") {
		assert(AnglesNormalizer.normalize(2, 1, 100, 2) == 2)
	}

	test("overallAngles: 1, minAmount: 1, maxAmount: 100, endpointsAmount: 2 ==> 2") {
		assert(AnglesNormalizer.normalize(1, 1, 100, 2) == 2)
	}

	test("overallAngles: 61, minAmount: 1, maxAmount: 100, endpointsAmount: 1 ==> 61") {
		assert(AnglesNormalizer.normalize(61, 1, 100, 1) == 61)
	}

	test("overallAngles: 1, minAmount: 1, maxAmount: 100, endpointsAmount: 1 ==> 1") {
		assert(AnglesNormalizer.normalize(1, 1, 100, 1) == 1)
	}

	test("overallAngles: 0, minAmount: 1, maxAmount: 100, endpointsAmount: 1 ==> 1") {
		assert(AnglesNormalizer.normalize(0, 1, 100, 1) == 1)
	}

	test("overallAngles: 110, minAmount: 1, maxAmount: 100, endpointsAmount: 1 ==> 100") {
		assert(AnglesNormalizer.normalize(110, 1, 100, 1) == 100)
	}

	test("overallAngles: 50, connectionsAmount: 2, connectionLimitations: ConnectionLimitations minAount: 1, maxAmount:100 ==> 25") {
		val limitations: ConnectionLimitations = getConnectionLimitations(1, 100)
		assert(AnglesNormalizer.normalize(50, 2, limitations) == 25)
	}

	test("overallAngles: 50, connectionsAmount: 1, connectionLimitations: ConnectionLimitations minAount: 1, maxAmount:100 ==> 50") {
		val limitations: ConnectionLimitations = getConnectionLimitations(1, 100)
		assert(AnglesNormalizer.normalize(50, 1, limitations) == 50)
	}

	test("overallAngles: 100, connectionsAmount: 2, connectionLimitations: ConnectionLimitations minAount: 1, maxAmount:100 ==> 50") {
		val limitations: ConnectionLimitations = getConnectionLimitations(1, 100)
		assert(AnglesNormalizer.normalize(100, 2, limitations) == 50)
	}

	test("overallAngles: 150, connectionsAmount: 2, connectionLimitations: ConnectionLimitations minAount: 1, maxAmount:100 ==> 75") {
		val limitations: ConnectionLimitations = getConnectionLimitations(1, 100)
		assert(AnglesNormalizer.normalize(150, 2, limitations) == 75)
	}

	test("overallAngles: 200, connectionsAmount: 2, connectionLimitations: ConnectionLimitations minAount: 1, maxAmount:100 ==> 100") {
		val limitations: ConnectionLimitations = getConnectionLimitations(1, 100)
		assert(AnglesNormalizer.normalize(200, 2, limitations) == 100)
	}

	test("overallAngles: 250, connectionsAmount: 2, connectionLimitations: ConnectionLimitations minAount: 1, maxAmount:100 ==> 100") {
		val limitations: ConnectionLimitations = getConnectionLimitations(1, 100)
		assert(AnglesNormalizer.normalize(250, 2, limitations) == 100)
	}

	private def getConnectionLimitations(minAmount: Int, maxAmount: Int) : ConnectionLimitations = {
		ConnectionLimitations(minAmount, maxAmount, "increaseF", "decreaseF", -1, -1, "priority", 30)
	}
}
