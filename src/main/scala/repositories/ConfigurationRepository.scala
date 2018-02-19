package repositories

import logging.ApplicationLogger
import models.internal.Connection
import serialization.ConnectionSerializer
import utils.{AppConfiguration, FileSystemService, Serialization}

trait ConfigurationRepository {
  def loadConnection(name: String): Either[String, Connection]
  def loadConnections(destination: String): Either[String, List[Connection]]
}

class FileConfigurationRepository(val fileService: FileSystemService) extends ConfigurationRepository {

  private val configurationLocation: String = new java.io.File(".").getCanonicalPath + AppConfiguration.connectionsLocation

  def loadConnection(name: String): Either[String, Connection] = {
    ApplicationLogger.info(s"${this.getClass} -> loading connection")
    fileService.filesByExtension(configurationLocation, ".json").right.flatMap { files =>
      val results: List[Either[String, List[Connection]]] = files.map { fileName => loadConnections(fileName) }
      Right(results)
    }.right.flatMap { list =>
      val errorsToConnections = list.foldRight((List.empty[String], List.empty[Connection])) {
        case (collector, (leftResult, rightResult)) =>
          collector.fold(left => (left :: leftResult, rightResult), right => (leftResult, right ::: rightResult))
      }
      Right(errorsToConnections)
    }.right.flatMap { case (errors, connections) =>
      if(errors.nonEmpty) Left(errors mkString "\n")
      else                findConnection(name, connections)
    }
  }

  private def findConnection(name: String, connections: List[Connection]) = {
    connections.find(conn => conn.info.name == name) match {
      case None => Left("Not found")
      case Some(retValue) =>
        if (retValue.info.is_active)  Right(retValue)
        else                          Left(s"${retValue.info.name} is deactivated")
    }
  }

  def loadConnections(destination: String): Either[String, List[Connection]] = {
    ApplicationLogger.info(s"${this.getClass} -> loading connections")
    for {
      data <- fileService.loadFile(s"$configurationLocation$destination").right
      decoded <- Serialization.decode[List[Connection]](data)(ConnectionSerializer.decodeConnectionList)
    } yield decoded
  }
}
