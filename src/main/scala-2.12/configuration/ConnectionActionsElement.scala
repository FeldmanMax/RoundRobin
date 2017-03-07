package configuration

case class ConnectionActionsElement(actionType: String, actionResolver: String, params: String) {
	def mappedParams: Map[String, String] = params.split(";").map(x=>x.split("=")).map(x=>x(0) -> x(1)).toMap

	override def toString: String = {
		s"actionType: $actionType, actionResolver: $actionResolver, params: $params"
	}
}
