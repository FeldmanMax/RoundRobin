package wrappers

import data_modules.RoundRobinDTO
import modules.{Connection, ConnectionsContainer}
import resolvers.Resolver
import utils.loggin.Log

class HttpWrapper(val retryMechanism: RetryMechanism[String], resolver: Resolver) {

	def get(connectionName: String, params: Option[Map[String, String]]): RoundRobinResult[String] = {
		val result: Option[RetryResult[String]] = retryMechanism.performAction(action, () => {}, "")

		result match {
			case Some(v) => RoundRobinResult(0, v.asInstanceOf[Option[String]])
			case None => RoundRobinResult(1, None)
		}
	}

	private def action: (String) => Option[RetryResult[String]] = {
		val func = (connectionName: String) => {
			val connection: Connection = ConnectionsContainer.getConnection(connectionName)
			val rrResult: RoundRobinDTO = connection.next()
			try{
				Option(RetryResult[String](isSuccess = true,
																	 resolver.resolve(rrResult.destination, rrResult.connectionTimeout, rrResult.commandTimeout)))
			}
			catch{
				case ex: Exception => Log.exception(connectionName, ex)
					Option(RetryResult[String](isSuccess = false, None))
			}
		}
		func
	}
}
