package utils

import repositories.FileConfigurationRepository
import services.ConfigurationService

trait ConfigurationServiceCreator {
  def configServiceWithFileConfiguration(): ConfigurationService = {
    new ConfigurationService(new FileConfigurationRepository(new FileSystemService))
  }
}
