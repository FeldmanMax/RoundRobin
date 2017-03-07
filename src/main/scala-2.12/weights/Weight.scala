package weights

import configuration.ConnectionLimitations
import data_modules.Endpoint
import utils.AnglesGenerator

abstract class Weight(val _connectionLimitations: ConnectionLimitations) {
	import utils.Operators.OperatorsExtensions

	private val _anglesGenerator: AnglesGenerator = new AnglesGenerator

	def increase(endpoint: Endpoint) : Endpoint
	def decrease(endpoint: Endpoint) : Endpoint

	protected def update(endpoint: Endpoint, func: (Endpoint) => Int) : Endpoint = {
		val generatedPointsAmount = func(endpoint)
		val amountOfPoints = getNumberOfPoints(endpoint, generatedPointsAmount)
		(endpoint.amountOfPoints == amountOfPoints) ? (endpoint,
																									 Endpoint(endpoint.name, endpoint.value, _anglesGenerator.generateAngles(amountOfPoints)))
	}

	private def getNumberOfPoints(endpoint: Endpoint, amountOfPoints: Int) : Int = {
		if(amountOfPoints >= _connectionLimitations.maxPointsAmount) return _connectionLimitations.maxPointsAmount
		else if(amountOfPoints <= _connectionLimitations.minPointsAmount) return _connectionLimitations.minPointsAmount
		amountOfPoints
	}
}
