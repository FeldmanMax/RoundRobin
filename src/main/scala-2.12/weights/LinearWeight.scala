package weights

import configuration.ConnectionLimitations
import data_modules.Endpoint

class LinearWeight(_connectionLimitations: ConnectionLimitations) extends Weight(_connectionLimitations){

	override def increase(endpoint: Endpoint): Endpoint = {
		val increaseFunction = (x: Endpoint) => {
			x.amountOfPoints + _connectionLimitations.increaseRatio
		}
		update(endpoint, increaseFunction)
	}

	override def decrease(endpoint: Endpoint): Endpoint = {
		val increaseFunction = (x: Endpoint) => {
			x.amountOfPoints - _connectionLimitations.decreaseRatio
		}
		update(endpoint, increaseFunction)
	}
}
