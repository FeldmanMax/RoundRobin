package utils.httpUtils

import utils.GeneralUtilities.Measurement

import scalaj.http.Http
import utils.loggin.Log

trait HttpRequestWrapper {
	def getAsString(url: String, connectionTimeout: Int, commandTimeout: Int) : Option[String] = {
		try{
			Measurement.measure[String]("HttpRequestWrapper.getAsString", () => {
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
