package utils

import com.typesafe.config.{Config, ConfigFactory}

object AppConfiguration {
  private val config: Config = ConfigFactory.load()

  def isProduction: Boolean = !isTest
  def isTest: Boolean = config.getString("env").toLowerCase == "test"
}
