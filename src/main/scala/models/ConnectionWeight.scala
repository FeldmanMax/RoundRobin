package models

case class ConnectionWeight(totalWeight: Int, endpointToMap: Map[String, EndpointWeight])
