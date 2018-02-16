package utils

import java.io.File

import logging.ApplicationLogger

import scala.io.{BufferedSource, Source}

class FileSystemService {
  def loadFile(path: String): Either[String, String] = {
    ApplicationLogger.info(s"${this.getClass} -> load file -> $path")
    try {
      val buffer: BufferedSource = Source.fromFile(path)
      try {
        val retValue: String = (buffer.getLines() mkString " ").toString
        ApplicationLogger.info(s"${this.getClass} -> load file -> $path -> FINISH OK!")
        Right(retValue)
      }
      finally buffer.close()
    }
    catch {
      case ex: Exception => Left(ex.getMessage)
    }
  }

  def filesByExtension(path: String, extension: String): Either[String, List[String]] = {
    try {
      val files: List[File] = new File(path).listFiles().filter(_.isFile).toList.filter(file => file.getName.contains(extension))
      val names: List[String] = files.map(x=>x.getName)
      Right(names)
    }
    catch {
      case ex: Exception => Left(ex.getMessage)
    }
  }

  def deleteFile(path: String): Either[String, Boolean] = {
    try{
      Right(new File(path).delete())
    }
    catch {
      case ex: SecurityException => Left(ex.getMessage)
    }
  }

  def createFile(name: String): Either[String, File] = {
    try {
      Right(new File(name))
    }
    catch {
      case ex: Exception => Left(ex.getMessage)
    }

  }
}
