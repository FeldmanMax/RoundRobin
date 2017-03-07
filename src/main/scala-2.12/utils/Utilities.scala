package utils

object Utilities {
	trait WrapperCommands {
		implicit def toResponse[TResult <: TResponse, TResponse](result: TResult): TResponse = result
		def tryCatch[TResult, TResponse](action: () => TResult,
		                                 exception: Exception => TResponse)
		                                (implicit toResponse: TResult  => TResponse): TResponse = {
			try{
				val actionResult: TResult = action()
				val response: TResponse = toResponse(actionResult)
				response
			}
			catch{
				case ex: Exception => exception(ex)
			}
		}
	}
}
