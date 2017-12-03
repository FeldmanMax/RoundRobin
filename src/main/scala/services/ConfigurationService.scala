package services

import models.Connection
import repositories.ConfigurationRepository

class ConfigurationService(val configRepository: ConfigurationRepository) {

  def loadConnection(connectionName: String): Either[String, Connection] = {
    configRepository.loadConfiguration(connectionName)
  }
}
