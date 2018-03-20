package services

import com.google.inject.Inject
import com.google.inject.name.Named
import logger.ApplicationLogger
import models.internal.Connection
import repositories.ConfigurationRepository

class ConfigurationService @Inject() (
                                       @Named("config_repository") val configRepository: ConfigurationRepository
                                     ) {

  def loadConnection(connectionName: String): Either[String, Connection] = {
    ApplicationLogger.info(s"${this.getClass} -> loading connection")
    configRepository.loadConnection(connectionName)
  }

  def loadConnections(fileName: String): Either[String, List[Connection]] = {
    configRepository.loadConnections(fileName)
  }
}
