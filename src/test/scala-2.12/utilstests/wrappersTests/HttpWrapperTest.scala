package utilstests.wrappersTests


import configuration.{ConnectionConfigurationElement, ConnectionLimitations}
import modules.ConnectionInformation
import org.scalatest.FunSuite
import resolvers.{Resolver, ResolverFactory}
import wrappers.RetryMechanism

class HttpWrapperTest extends FunSuite {
	test("1. Success on first request") {
		val retryMechanism: RetryMechanism[String] = new RetryMechanism[String] {
			override val connectionInfo: ConnectionInformation = getConnectionInformation()

		}
		val resolver:Resolver = ResolverFactory.get("http")
		resolver.resolve("command", 100, 100)
	}

	private def getConnectionInformation(): ConnectionInformation = {
		val connectionInfo: ConnectionInformation = ConnectionInformation(ConnectionConfigurationElement(name = "name",
			region= "region",
			connectionTimeoutInMillis=100,
			commandTimeoutInMillis=100,
			retries= 5,
			endpoints = List.empty,
			connectionLimitations = new ConnectionLimitations(minPointsAmount = 100,
				maxPointsAmount = 100,
				increaseFunction = "linear",
				decreaseFunction = "linear",
				increaseRatio = 50,
				decreaseRatio = 50,
				priority = "equal",
				minConnectionWeight = 20),
			actions = null))
		connectionInfo
	}
}
