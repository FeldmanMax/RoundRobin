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
    fileService.filesByExtension(configurationLocation, ".json").right.flatMap { files =>
      val results: List[Either[String, List[Connection]]] = files.map { fileName =>
        fileService.loadFile(s"$configurationLocation$fileName").right.flatMap { data =>
          Right(JsonSerialization.deserialize[List[Connection]](data))
        }
      }
      Right(results)
    }.right.flatMap { list =>
      val errorsToConnections = list.foldRight((List.empty[String], List.empty[Connection])) {
        case (collector, (leftResult, rightResult)) =>
          collector.fold(left => (left :: leftResult, rightResult), right => (leftResult, right ::: rightResult))
      }
      Right(errorsToConnections)
    }.right.flatMap { case (errors, connections) =>
      if(errors.nonEmpty) Left(errors mkString "\n")
      else                connections.find(conn => conn.info.name == name) match {
                            case None => Left("Not found")
                            case Some(retValue) => Right(retValue)
                          }
    }
  }

  def loadConnections(destination: String): Either[String, List[Connection]] = {
    fileService.loadFile(s"$configurationLocation$destination.json").right.flatMap { data =>
      Right(JsonSerialization.deserialize[List[Connection]](data))
    }
  }
}
