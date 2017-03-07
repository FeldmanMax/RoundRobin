package utils

import configuration.ConnectionLimitations

object AnglesNormalizer {
	def normalize(overallAngles: Int, minAmount: Int, maxAmount: Int, endpointsAmount: Int): Int = {
		val totalMinAmount: Int = minAmount * endpointsAmount
		val totalMaxAmount: Int = maxAmount * endpointsAmount
		val totalAngles: Int = getAnglesAmount(overallAngles / endpointsAmount, minAmount, maxAmount)
		getAnglesAmount(totalAngles, totalMinAmount, totalMaxAmount)
	}

	def normalize(connectionLimitations: ConnectionLimitations, anglesToCreate: Int) : Int = {
		getAnglesAmount(anglesToCreate, connectionLimitations.maxPointsAmount, connectionLimitations.maxPointsAmount)
	}

	def normalize(overallAngles: Int, connectionsAmount: Int, connectionLimitations: ConnectionLimitations) : Int = {
		val minAmount: Int = connectionLimitations.minPointsAmount
		val maxAmount: Int = connectionLimitations.maxPointsAmount
		normalize(overallAngles, minAmount, maxAmount, connectionsAmount)
	}

	private def getAnglesAmount(angles: Int, minAmount: Int, maxAmount: Int) : Int = {
		if(angles <= minAmount) minAmount
		else if(angles >= maxAmount) maxAmount
		else angles
	}
}
