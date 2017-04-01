package utils.httpUtils

import scalaj.http.Http
import utils.loggin.Log
import utils.GeneralUtilities.Measurement

trait HttpRequestWrapper extends Measurement {
	def getAsString(url: String, connectionTimeout: Int, commandTimeout: Int) : Option[String] = {
		try{
			measure[String]("HttpRequestWrapper.getByJson", () => {
				val response = Http(url).timeout(connectionTimeout, commandTimeout).asString.body
				Some(response)
			})
		}
		catch{
			case ex: Exception => Log.exception(s"$url got an exception", ex)
				None
		}
	}
}
