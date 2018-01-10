package services

import logging.Logger
import models.Connection
import org.slf4j.event.Level
import repositories.ConfigurationRepository

class ConfigurationService(val configRepository: ConfigurationRepository) {

  def loadConnection(connectionName: String): Either[String, Connection] = {
    Logger.info(s"${this.getClass} -> loading connection")
    configRepository.loadConnection(connectionName)
  }

  def loadConnections(destination: String): Either[String, List[Connection]] = {
    configRepository.loadConnections(destination)
  }
}
