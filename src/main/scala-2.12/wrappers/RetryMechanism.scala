package wrappers

import modules.ConnectionInformation
import utils.loggin.Log

trait RetryMechanism[TResult] {

	val connectionInfo: ConnectionInformation

	def performAction(action: (String) => Option[RetryResult[TResult]], postAction: () => Unit, commandName: String): Option[RetryResult[TResult]] = {
		val maxRetries = connectionInfo.configurationElement.retries
		var currentTry: Int = 0
		var result: Option[RetryResult[TResult]] = Option(RetryResult[TResult](false, None))
		try {
			while (currentTry < maxRetries) {
				action(connectionInfo.name) match {
					case Some(retryResult) =>
						if (retryResult.isSuccess) {
							result = Option(retryResult)
							currentTry = maxRetries
						}

					case None => Log.exception(commandName + s" failed try number $currentTry")
				}
				currentTry = currentTry + 1
			}
			result
		}
		finally{
			postAction()
		}
	}
}
