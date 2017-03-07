package utils.httpUtils

import scalaj.http.Http
import spray.json._
import utils.loggin.Log
import utils.GeneralUtilities.Measurement

trait HttpRequestWrapper extends Measurement {
	def getByJson[TResult](url: String, connectionTimeout: Int, commandTimeout: Int) : Option[TResult] = {
		try{
			measure("HttpRequestWrapper.getByJson", () => {
//				val responseAsJson = Http(url).timeout(connectionTimeout, commandTimeout).asString.toJson
//				Some(responseAsJson.convertTo[TResult])
				None
			})
		}
		catch{
			case ex: Exception => Log.exception(s"$url got an exception", ex)
				None
		}
	}
}
