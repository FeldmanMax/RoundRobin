package utils

object StringUtils {

	def replace(origin: String, params: Map[String, String]) : String = {
		var retValue: String = origin
		for ((key, value) <- params) {
			retValue = replaceImpl(retValue, key, value)
		}
		retValue
	}

	private def replaceImpl(origin: String, key: String, value: String): String = {
		origin.replace(key, value)
	}
}
