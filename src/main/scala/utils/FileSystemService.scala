package utils

import java.io.{Closeable, File}

import utils.Implicits.CloseableExtension

import scala.io.BufferedSource

class FileSystemService {
  def loadFile(path: String): Either[String, String] = {
    io.Source.fromFile(path).using[String]((source: Closeable) => source.asInstanceOf[BufferedSource].getLines() mkString "")
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
}
