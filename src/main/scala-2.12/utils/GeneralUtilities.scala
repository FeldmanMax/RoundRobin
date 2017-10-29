package utils

import org.joda.time.DateTime
import utils.loggin.Log

object GeneralUtilities {

	object Measurement {
		def measure(name: String, func: () => Unit) = {
			val dateTime: DateTime = new DateTime()
			val startTime: Int = dateTime.millisOfDay().get()
			func()
			val endTime: Int = dateTime.millisOfDay().get()
			Log.info(s"Measurement: $name ${endTime - startTime}(ms)")
		}

		def measure[TResult](name: String, func: () => Option[TResult]): Option[TResult] = {
			val dateTime: DateTime = new DateTime()
			val startTime: Int = dateTime.millisOfDay().get()
			val result:Option[TResult] = func()
			val endTime: Int = dateTime.millisOfDay().get()
			Log.info(s"Measurement: $name ${endTime - startTime}(ms)")
			result
		}
	}
}
