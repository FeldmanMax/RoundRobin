package utils

import utils.loggin.Log

object Utilities {

	trait WrapperCommands {
		def toResponse[TResult <: TResponse, TResponse](result: TResult): TResponse = result
		def tryCatch[TResult, TResponse](action: () => TResult,
		                                 exception: Exception => TResponse)
		                                (implicit toResponse: TResult  => TResponse): TResponse = {
			try{
				val actionResult: TResult = action()
				toResponse(actionResult)
			}
			catch{
				case ex: Exception =>
					Log.exception(ex.getMessage, ex)
					exception(ex)
			}
		}
	}
}