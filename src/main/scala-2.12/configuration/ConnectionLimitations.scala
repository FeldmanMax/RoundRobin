package configuration

case class ConnectionLimitations(minPointsAmount: Int,
                                 maxPointsAmount: Int,
                                 increaseFunction: String,
                                 decreaseFunction: String,
                                 increaseRatio: Int,
                                 decreaseRatio: Int,
                                 priority: String,
                                 minConnectionWeight: Int) {
	override def toString: String = {
		s"MinEndoints: $minPointsAmount, MaxEndpoints: $maxPointsAmount" +
			s"IncreaseFunc: $increaseFunction, DecreaseFunc: $decreaseFunction" +
			s"IncreaseRatio: $increaseRatio, DecreaseRatio: $decreaseRatio, Priority: $priority, MinConnWeight: $minConnectionWeight"
	}
}
