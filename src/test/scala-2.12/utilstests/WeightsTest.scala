package utilstests

import configuration.ConnectionLimitations
import data_modules.Endpoint
import org.scalatest.FunSuite
import utils.AnglesGenerator
import weights.{LinearWeight, Weight, WeightGenerator}

class WeightsTest extends FunSuite {
	test("Lineal weight test - increase from 50 to 55"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Linear", 5, 10, "priority", 20)
		val linearWeight: Weight = new LinearWeight(connectionLimitations)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(50))
		assert(linearWeight.increase(endpoint).amountOfPoints == 55)
	}

	test("Lineal weight test - decrease from 50 to 40"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Linear", 5, 10, "prioeity", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(50))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val linearWeight: Weight = weightGenerator.generateIncrease()
		val result = linearWeight.decrease(endpoint)
		assert(result.amountOfPoints == 40)
	}

	test("Lineal weight test - 100 to 100"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Linear", 5, 10, "prioeity", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(100))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val linearWeight: Weight = weightGenerator.generateIncrease()
		val result = linearWeight.increase(endpoint)
		assert(result.amountOfPoints == 100)
		assert(result == endpoint)
	}

	test("Lineal weight test - 5 to 1"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Linear", 5, 10, "prioeity", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(5))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val linearWeight: Weight = weightGenerator.generateIncrease()
		val result = linearWeight.decrease(endpoint)
		assert(result.amountOfPoints == 1)
	}

	test("Lineal weight test - 1 to 1"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Linear", 5, 10, "prioeity", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(1))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val linearWeight: Weight = weightGenerator.generateIncrease()
		val result = linearWeight.decrease(endpoint)
		assert(result.amountOfPoints == 1)
		assert(result == endpoint)
	}

	test("Lineal weight test - 95 to 100"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Linear", 5, 10, "prioeity", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(95))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val linearWeight: Weight = weightGenerator.generateIncrease()
		val result = linearWeight.increase(endpoint)
		assert(result.amountOfPoints == 100)
	}

	test("Exponential weight test - 100 to 5 to 50"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Exponential", "Exponential", 10, 20, "priority", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(100))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val exponentialWeight: Weight = weightGenerator.generateIncrease()
		var result = exponentialWeight.decrease(endpoint)

		assert(result.amountOfPoints == 5)
		result = exponentialWeight.increase(result)
		assert(result.amountOfPoints == 50)
	}

	test("Exponential weight test - 100 to 5 to 1"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Exponential", "Exponential", 40, 20, "priority", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(100))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val exponentialWeight: Weight = weightGenerator.generateIncrease()
		var result = exponentialWeight.decrease(endpoint)

		assert(result.amountOfPoints == 5)
		result = exponentialWeight.decrease(result)
		assert(result.amountOfPoints == 1)
	}

	test("Exponential weight test - 100 to 100 to 100"){
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Exponential", "Exponential", 40, 20, "priority", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(100))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val exponentialWeight: Weight = weightGenerator.generateIncrease()
		var result = exponentialWeight.increase(endpoint)
		assert(result.amountOfPoints == 100)
		assert(result == endpoint)
		result = exponentialWeight.increase(result)
		assert(result.amountOfPoints == 100)
		assert(result == endpoint)
	}

	test("Exponential decrease 50% and Linear increase by 10") {
		val connectionLimitations: ConnectionLimitations = ConnectionLimitations(1, 100, "Linear", "Exponential", 10, 2, "priority", 20)
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val endpoint: Endpoint = Endpoint("name", "value", anglesGenerator.generateAngles(100))
		val weightGenerator: WeightGenerator = new WeightGenerator(connectionLimitations)
		val increase = weightGenerator.generateIncrease()
		val decrease = weightGenerator.generateDecrease()
		var result = decrease.decrease(endpoint)
		assert(result.amountOfPoints == 50)
		result = increase.increase(result)
		assert(result.amountOfPoints == 60)
	}
}
