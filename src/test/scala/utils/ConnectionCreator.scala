package utils

import models.KeyValue
import models.internal._
import serialization.{ConnectionSerializer, Serialization}

trait ConnectionCreator extends WeightCreator {
	def getConnection(generalInfo: ConnectionGeneralInfo, nameToWeight: Map[String, Int], metadata: ConnectionMetadata[String] = ConnectionMetadata(List.empty)): Connection = {
		val endpoints: Map[String, ConnectionEndpoint] = nameToWeight
			.map { case (name, weight) => getWeight(name, weight) }
			.map { weight => weight.endPointName -> ConnectionEndpoint(KeyValue[String](weight.endPointName, ""), true) }.toMap
		Connection(generalInfo, endpoints.values.toList, metadata)
	}

  def getDefaultConnection(name: String): Connection = getConnection(ConnectionGeneralInfo(name), Map("first" -> 100))

	def getSerializedConnection(): String = {
    val connection: Connection = getConnection(ConnectionGeneralInfo("test"), Map("first" -> 100))
		Serialization.encode[Connection](connection)(ConnectionSerializer.encodeConnection) match {
			case Left(error) => throw new Exception(error)
			case Right(data) => data.toString()
 		}
	}

	def getSerializedConnection(generalInfo: ConnectionGeneralInfo, nameToWeight: Map[String, Int]): String = {
		val connection: Connection = getConnection(generalInfo, Map("first" -> 100))
		Serialization.encode[Connection](connection)(ConnectionSerializer.encodeConnection) match {
			case Left(error) => throw new Exception(error)
			case Right(data) => data.toString()
		}
	}
}
