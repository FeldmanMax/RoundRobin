package services

import com.typesafe.config.{Config, ConfigFactory}
import models.Connection
import utils.Serialization.JsonSerialization

trait ConnectionLoader {
  def loadConnection(connectionName: String): Either[String, Connection]
}

class ConfigConnectionLoader(weightService: WeightService) extends ConnectionLoader {
  override def loadConnection(connectionName: String): Either[String, Connection] = {
//    val config: Config = ConfigFactory.load()
//    val connection: Connection = JsonSerialization.deserialize(config.getString(connectionName))
    Left("")
  }
}

class ConnectionLoaderMock() extends ConnectionLoader {
  override def loadConnection(connectionName: String): Either[String, Connection] = {
    connectionName match {
      case "oneEndpoint100%" => getOneEndpoint100Percent
    }


    Left("")
  }

  private def getOneEndpoint100Percent: Either[String, Connection] = {
    Left("")
  }
}
