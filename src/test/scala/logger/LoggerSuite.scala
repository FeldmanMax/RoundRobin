package logger

import org.scalatest.FunSuite
import org.slf4j.event.Level
import services.FileSystemService

class LoggerSuite extends FunSuite {
  private val log_name_prefix: String = "infra_base"
  private val log_file_name_template: String = s"/tmp/${log_name_prefix}_<level>_test.log"

  test("read/write into info/error/debug files") {
    val fileService: FileSystemService = new FileSystemService()
    val fileLogger: FileLogger = new FileLogger
    val data: String = "here is my info"
    fileLogger.log(Level.INFO, data)
    fileLogger.log(Level.DEBUG, data)
    fileLogger.log(Level.ERROR, data)
    checkFile(fileService, data, "info")
    checkFile(fileService, data, "debug")
    checkFile(fileService, data, "error")
  }

  test("all the lefts") {
    assert(ApplicationLogger.debugLeft[String]("debug left").left.get == "debug left")
    assert(ApplicationLogger.infoLeft[String]("info left").left.get == "info left")
    assert(ApplicationLogger.errorLeft[String]("error left").left.get == "error left")
    assert(ApplicationLogger.traceLeft[String]("trace left").left.get == "trace left")
    assert(ApplicationLogger.warnLeft[String]("warn left").left.get == "warn left")
  }

  private def checkFile(fileService: FileSystemService, data: String, level: String) = {
    val fileToRead: String = log_file_name_template.replace("<level>", level)
    fileService.loadFile(fileToRead) match {
      case Left(left) => fail(left)
      case Right(response) => assert(response.contains(data), s" ---> $level failed")
    }
  }
}
