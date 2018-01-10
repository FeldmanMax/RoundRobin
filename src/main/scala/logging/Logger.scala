package logging

import org.slf4j
import org.slf4j.event.Level
import utils.AppConfiguration

trait LoggerBase {
  def log[T <: AnyRef](level: Level, data: T)
  def log(level: Level, data: String)
}

class ThreadLogger extends FileLogger {
  override def log[T <: AnyRef](level: Level, data: T): Unit = {
    log(level, data.toString)
  }

  override def log(level: Level, data: String): Unit = {
    super.log(level, "Thread #" + Thread.currentThread().getId() + " -> " + data)
  }
}

class FileLogger extends LoggerBase {
  override def log[T <: AnyRef](level: Level, data: T): Unit = {
    logImpl(level, data.toString, slf4j.LoggerFactory.getLogger(level.toString.toLowerCase))
  }

  override def log(level: Level, data: String): Unit = {
    logImpl(level, data, slf4j.LoggerFactory.getLogger(level.toString.toLowerCase))
  }

  private def logImpl(level: Level, data: String, logger: slf4j.Logger): Unit = {
    level match {
      case Level.DEBUG => logger.debug(data)
      case Level.ERROR => logger.error(data)
      case Level.INFO =>  logger.info(data)
      case Level.TRACE => logger.trace(data)
      case Level.WARN =>  logger.warn(data)
    }
  }
}

object FileLogger extends FileLogger

class MockedLogger extends LoggerBase {
  override def log[T <: AnyRef](level: Level, data: T): Unit = {}
  override def log(level: Level, data: String): Unit = {}
}

class Logger(val loggers: List[LoggerBase]) extends LoggerBase {

  override def log[T <: AnyRef](level: Level, data: T): Unit = {
    loggers.foreach { logger => logger.log(level, data) }
  }

  override def log(level: Level, data: String): Unit = {
    loggers.foreach { logger => logger.log(level, data) }
  }
}

object LoggerFactory {
  def get: Logger = {
    val loggers: List[LoggerBase] = AppConfiguration.loggingDestinations.map {
      case FileDestination => new ThreadLogger()
      case DoNotLog =>        new MockedLogger()
      case _ =>               new MockedLogger()
    }
    new Logger(loggers)
  }
}

object Logger {
  private val logger: Logger = LoggerFactory.get
  def log[T <: AnyRef](level: Level, data: T): Unit = logger.log(level, data)
  def log(level: Level, data: String): Unit = logger.log(level, data)
}