package data_modules

case class Endpoint(name: String, value: String, points: List[Angle]) {
	def isTopAnglesAmount(maxAmount: Int) : Boolean = points.length >= maxAmount
	def isBottomAnglesAmount(minAmount: Int) : Boolean = points.length <= minAmount
	def amountOfPoints: Int = points.length
}
