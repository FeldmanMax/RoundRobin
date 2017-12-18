package services

import models.Connection
import repositories.ConfigurationRepository

class ConfigurationService(val configRepository: ConfigurationRepository) {

  def loadConnection(connectionName: String): Either[String, Connection] = {
    configRepository.loadConnection(connectionName)
  }

  def loadConnections(destination: String): Either[String, List[Connection]] = {
    configRepository.loadConnections(destination)
  }
}
