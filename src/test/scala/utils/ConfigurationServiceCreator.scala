package utils

import repositories.FileConfigurationRepository
import services.{ConfigurationService, FileSystemService}

trait ConfigurationServiceCreator {
  def configServiceWithFileConfiguration(): ConfigurationService = {
    new ConfigurationService(new FileConfigurationRepository(new FileSystemService))
  }
}
