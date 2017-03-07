package weights

import javax.naming.ConfigurationException

import configuration.ConnectionLimitations

class WeightGenerator(_connectionLimitations: ConnectionLimitations) {
	def generateIncrease(): Weight = {
		generateImpl(_connectionLimitations.increaseFunction)
	}

	def generateDecrease(): Weight = {
		generateImpl(_connectionLimitations.decreaseFunction)
	}

	private def generateImpl(weightType: String) : Weight = {
		weightType match {
			case "Linear" => new LinearWeight(_connectionLimitations)
			case "Exponential" => new ExponentialWeight(_connectionLimitations)
			case _ => throw new ConfigurationException(_connectionLimitations.increaseFunction + " is not supported")
		}
	}
}
