package weights

import configuration.ConnectionLimitations
import data_modules.Endpoint

class ExponentialWeight (_connectionLimitations: ConnectionLimitations) extends Weight(_connectionLimitations){
	override def increase(endpoint: Endpoint): Endpoint = {
		update(endpoint, increaseFunction)
	}

	override def decrease(endpoint: Endpoint): Endpoint = {
		update(endpoint, decreaseFunction)
	}

	private def increaseFunction(x: Endpoint) : Int = {
		val result: Int = x.amountOfPoints * _connectionLimitations.increaseRatio
		result
	}

	private def decreaseFunction(x: Endpoint) : Int = {
		val result: Int = x.amountOfPoints / _connectionLimitations.decreaseRatio
		result
	}
}
