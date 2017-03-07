package utils.loggin

import com.typesafe.scalalogging.LazyLogging

object Log extends LazyLogging {
	def info(data: String) = logger.info(data)
	def debug(data: String) = logger.debug(data)
	def exception(data: String, ex: Exception) = logger.error(data, ex)
}
