package repositories

import models.Connection
import utils.Serialization.JsonSerialization
import utils.{AppConfiguration, FileSystemService}

trait ConfigurationRepository {
  def loadConnection(name: String): Either[String, Connection]
  def loadConnections(destination: String): Either[String, List[Connection]]
}

class FileConfigurationRepository(val fileService: FileSystemService) extends ConfigurationRepository {

  private val configurationLocation: String = {
    val path: String = new java.io.File(".").getCanonicalPath
    path + "/src/" + (if(AppConfiguration.isTest) "test" else "main") + "/resources/"
  }

  def loadConnection(name: String): Either[String, Connection] = {
    fileService.loadFile(s"$configurationLocation$name.json").right.flatMap { data =>
      Right(JsonSerialization.deserialize[Connection](data))
    }
  }

  def loadConnections(destination: String): Either[String, List[Connection]] = {
    fileService.loadFile(s"$configurationLocation$destination.json").right.flatMap { data =>
      Right(JsonSerialization.deserialize[List[Connection]](data))
    }
  }
}
