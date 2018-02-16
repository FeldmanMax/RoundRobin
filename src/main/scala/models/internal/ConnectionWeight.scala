package models.internal

import roundrobin.models.api.EndpointWeight

case class ConnectionWeight(totalWeight: Int, endpointToMap: Map[String, EndpointWeight])
