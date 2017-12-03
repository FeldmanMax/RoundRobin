package repositories

import models.Connection
import utils.Serialization.JsonSerialization
import utils.{AppConfiguration, FileSystemService}

trait ConfigurationRepository {
  def loadConfiguration(name: String): Either[String, Connection]
}

class FileConfigurationRepository(val fileService: FileSystemService) extends ConfigurationRepository {

  private val configurationLocation: String = {
    val path: String = new java.io.File(".").getCanonicalPath
    path + "/src/" + (if(AppConfiguration.isTest) "test" else "main") + "/resources/"
  }

  def loadConfiguration(name: String): Either[String, Connection] = {
    fileService.loadFile(s"$configurationLocation$name.json").right.flatMap { data =>
      Right(JsonSerialization.deserialize[Connection](data))
    }
  }
}
