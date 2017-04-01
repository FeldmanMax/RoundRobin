package resolvers

import utils.httpUtils.HttpRequestWrapper

class HttpResolver(val connectionTimeout: Int,
                   val commandTimeout: Int)
	extends HttpRequestWrapper
		with Resolver{

	override def resolve(command: String): Option[String] = {
		getAsString(command, commandTimeout, commandTimeout)
	}
}
