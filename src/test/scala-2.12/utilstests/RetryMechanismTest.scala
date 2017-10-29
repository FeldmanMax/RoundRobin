package utilstests

import configuration.{ConnectionConfigurationElement, ConnectionLimitations}
import modules.ConnectionInformation
import org.scalatest.FunSuite
import wrappers.{RetryMechanism, RetryResult}

class RetryMechanismTest extends FunSuite {
	test("1. First Success") {
		val retryMechanism: RetryMechanism[String] = new RetryMechanism[String]() {
			override val connectionInfo: ConnectionInformation = getConnectionInformation()
		}

		var retry: Int = 1
		val func = (s: String) => {
			if (retry == 1) {
				retry = retry + 1
				Option(RetryResult[String](true, Some(s)))
			}
			else {
				Option(RetryResult[String](false, Some("shit")))
			}
		}

		val result: Option[RetryResult[String]] = retryMechanism.performAction(func, () => {}, "")
		assert(result.getOrElse(null).isSuccess && result.getOrElse(null).result.getOrElse("") == "name")
	}

	test("2. On second try Success") {
		val retryMechanism: RetryMechanism[String] = new RetryMechanism[String]() {
			override val connectionInfo: ConnectionInformation = getConnectionInformation()
		}

		var retry: Int = 1
		val func = (s: String) => {
			if (retry == 2) {
				Option(RetryResult[String](true, Some(s)))
			}
			else {
				retry = retry + 1
				Option(RetryResult[String](false, Some("shit")))
			}
		}

		val result: Option[RetryResult[String]] = retryMechanism.performAction(func, () => {}, "")
		assert(result.getOrElse(null).isSuccess && result.getOrElse(null).result.getOrElse("") == "name")
	}

	test("3. Failure") {
		val retryMechanism: RetryMechanism[String] = new RetryMechanism[String]() {
			override val connectionInfo: ConnectionInformation = getConnectionInformation()
		}

		val func = (s: String) => {
				Option(RetryResult[String](false, Some("shit")))
		}

		val result: Option[RetryResult[String]] = retryMechanism.performAction(func, () => {}, "")
		assert(!result.getOrElse(null).isSuccess && result.getOrElse(null).result.isEmpty)
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
