package resolvers

import utils.httpUtils.HttpRequestWrapper

class HttpResolver() extends HttpRequestWrapper with Resolver{
	override def resolve(command: String, connectionTimeout: Int, commandTimeout: Int): Option[String] = {
		getAsString(command, commandTimeout, commandTimeout)
	}
}
