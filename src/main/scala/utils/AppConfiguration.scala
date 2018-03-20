package utils

import com.typesafe.config.{Config, ConfigFactory}
import logger.{DoNotLog, FileDestination, LogDestination}

object AppConfiguration {
  private val config: Config = ConfigFactory.load()

  def isProduction: Boolean = !isTest
  def isTest: Boolean = config.getString("env").toLowerCase == "test"
  def loggingDestinations: List[LogDestination] = {
    try {
      config.getString("loggingDestinations").split(",").toList.map { single =>
        single.toLowerCase match {
          case "file"     => FileDestination
          case "donotlog" => DoNotLog
          case _          => DoNotLog
        }
      }
    }
    catch {
      case _: Exception => List(DoNotLog)
    }
  }

  def connectionsLocation: String = config.getString("connectionsLocation")
}
