package utils

import java.io.Closeable

import utils.Implicits.CloseableExtension

import scala.io.BufferedSource

class FileSystemService {
  def loadFile(path: String): Either[String, String] = {
    io.Source.fromFile(path).using[String]((source: Closeable) => source.asInstanceOf[BufferedSource].getLines() mkString "")
  }
}
