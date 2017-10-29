package data_modules

case class RoundRobinDTO(destination: String,
                         isSuccess: Boolean,
                         endpointName: String,
                         connectionName: String,
                         connectionTimeout: Int,
                         commandTimeout: Int)
{}
