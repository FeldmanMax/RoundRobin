package services

import com.google.inject.Inject
import com.google.inject.name.Named
import logging.Logger
import models.Connection
import repositories.ConfigurationRepository

class ConfigurationService @Inject() (
                                       @Named("config_repository") val configRepository: ConfigurationRepository
                                     ) {

  def loadConnection(connectionName: String): Either[String, Connection] = {
    Logger.info(s"${this.getClass} -> loading connection")
    configRepository.loadConnection(connectionName)
  }

  def loadConnections(destination: String): Either[String, List[Connection]] = {
    configRepository.loadConnections(destination)
  }
}
