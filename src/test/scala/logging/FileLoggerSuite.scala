package logging

import models.Point
import org.scalatest.FunSuite
import org.slf4j.event.Level
import utils.FileSystemService

class FileLoggerSuite extends FunSuite {
  private val fileSystemService: FileSystemService = new FileSystemService()
  private val path_template: String = "/tmp/log_%s.out"

  test("log info to a file") {
    FileLogger.log(Level.INFO, "into test")
    fileSystemService.loadFile(path_template.format("info")) match {
      case Left(_) => assert(false, "File could not be loaded")
      case Right(content) => assert(content.contains("into test"))
    }
  }

  test("error info to a file") {
    FileLogger.log(Level.ERROR, "error test")
    fileSystemService.loadFile(path_template.format("error")) match {
      case Left(_) => assert(false, "File could not be loaded")
      case Right(content) => assert(content.contains("error test"))
    }
  }

  test("write a Point case class into a error file log") {
    FileLogger.log(Level.ERROR, Point(1.0, 2.0))
    fileSystemService.loadFile(path_template.format("error")) match {
      case Left(_) => assert(false, "File could not be loaded")
      case Right(content) => assert(content.contains("(x:1.0, y:2.0)"))
    }
  }
}