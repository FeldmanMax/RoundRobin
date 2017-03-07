package utils

import configuration.ConnectionLimitations
import data_modules.Angle

import scala.util.Random

class AnglesGenerator {

	private val random = new Random()

	def generateAngles(amount: Int): List[Angle] = {
		val angles = scala.collection.mutable.ArrayBuffer.empty[Angle]
		for(i <- 1 to amount){
			angles += Angle(random.nextDouble() * (math.Pi * 2))
		}
		angles.toList
	}

	def generateAngle(): Angle = generateAngles(1).head

	def generateAngles(connectionLimitations: ConnectionLimitations, anglesToCreate: Int) : List[Angle] = {
		val angles: Int = AnglesNormalizer.normalize(connectionLimitations, anglesToCreate)
		generateAngles(angles)
	}

	def generateAngles(overallAngles: Int, minAmount: Int, maxAmount: Int, endpointsAmount: Int) : List[Angle] = {
		val anglesToCreate: Int = AnglesNormalizer.normalize(overallAngles, minAmount, maxAmount, endpointsAmount)
		generateAngles(anglesToCreate)
	}
}
