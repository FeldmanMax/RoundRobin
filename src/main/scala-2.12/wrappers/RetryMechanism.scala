package wrappers

import modules.ConnectionInformation

trait RetryMechanism {

	val connectionInfo: ConnectionInformation = _

	def performAction[TResult](action: () => Option[RetryResult[TResult]], postAction: () => Unit, commandName: String): Option[TResult] = {
		val maxRetries = connectionInfo.configurationElement.retries
		var currentTry: Int = 1
		try {
			var result: Option[TResult] = None
			while(currentTry <= maxRetries) {
				action() match {
					case Some(retryResult) => {
						if(retryResult.isSuccess)
							result = retryResult.result
					}
				}

			}
			result
		}
		finally{
			postAction()
		}
	}

	private def internalPostAction(command: String, amountOfRetries: Int, exception: Option[Exception] = None): Unit = {

	}
}
