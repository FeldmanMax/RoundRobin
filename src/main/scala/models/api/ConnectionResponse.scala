package roundrobin.models.api

final case class ConnectionResponse(parentConnectionName: String, connectionName: String, endpointName: String, value: String)
