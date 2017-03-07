package data_modules

case class Angle(angle: Double) {
	def completeAngle: Double = 2*math.Pi - angle
}
