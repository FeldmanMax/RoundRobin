package utils

import models.{Connection, ConnectionEndpoint, ConnectionGeneralInfo}
import utils.Serialization.JsonSerialization

trait ConnectionCreator extends WeightCreator {
	def getConnection(generalInfo: ConnectionGeneralInfo, nameToWeight: Map[String, Int]): Connection = {
		val endpoints: Map[String, ConnectionEndpoint] = nameToWeight
			.map { case (name, weight) => getWeight(name, weight) }
			.map { weight => weight.endPointName -> ConnectionEndpoint(weight.endPointName, "") }.toMap
		Connection(generalInfo, endpoints.values.toList)
	}

  def getDefaultConnection(name: String): Connection = getConnection(ConnectionGeneralInfo(name), Map("first" -> 100))

	def getSerializedConnection(): String = {
    val connection: Connection = getConnection(ConnectionGeneralInfo("test"), Map("first" -> 100))
    JsonSerialization.serialize(connection)
	}

	def getSerializedConnection(generalInfo: ConnectionGeneralInfo, nameToWeight: Map[String, Int]): String = {
		val connection: Connection = getConnection(generalInfo, Map("first" -> 100))
		JsonSerialization.serialize(connection)
	}
}
