package utilstests

import configuration.{Configuration, ConnectionConfigurationElement}
import org.scalatest.FunSuite

class ConfigurationTest extends FunSuite{
	import utils.DataStructure._

	test("Load CSPider_SimpleEndpoints") {
		val element: ConnectionConfigurationElement = getConnectionByName("CSPider_SimpleEndpoints")

		assert(element.name == "CSPider_SimpleEndpoints", "Test1")
		assert(element.connectionTimeoutInMillis == 1000, "Test2")
		assert(element.endpoints.length == 2, "Test3")
		assert(element.endpoints.count(x=>x.region == "HK") == 1, "Test4")
		assert(element.endpoints.filter(x=>x.region == "HK").head.endpoints.length == 2, "Test5")
		assert(element.endpoints.count(x=>x.region == "SG") == 1, "Test6")
		assert(element.endpoints.filter(x=>x.region == "SG").head.endpoints.length == 2, "Test7")
		assert(element.region == "", "Test8")
	}

	test("Load CSPider_HK") {
		val element: ConnectionConfigurationElement = getConnectionByName("CSPider_HK")

		assert(element.name == "CSPider_HK", "Test1")
		assert(element.connectionTimeoutInMillis == 1000, "Test2")
		assert(element.endpoints.length == 1, "Test3")
		assert(element.endpoints.filterHead((x)=>x.region == "HK").get.endpoints.length == 2, "Test4")
		assert(element.region == "HK", "Test5")
	}

	test("Load CSPider_SG") {
		val element: ConnectionConfigurationElement = getConnectionByName("CSPider_SG")

		assert(element.name == "CSPider_SG", "Test1")
		assert(element.connectionTimeoutInMillis == 1000, "Test2")
		assert(element.endpoints.length == 1, "Test3")
		assert(element.endpoints.filterHead((x)=>x.region == "SG").get.endpoints.length == 2, "Test4")
		assert(element.region == "SG", "Test5")
	}

	test("Load AGCDB") {
		val element: ConnectionConfigurationElement = getConnectionByName("AGCDB")
		assert(element.name == "AGCDB", "Test1")
		assert(element.connectionTimeoutInMillis == 1000, "Test2")
		assert(element.region == "", "Test3")
		assert(element.endpoints.length == 2, "Test4")
		assert(element.endpoints.filterHead((x)=>x.region == "HK").get.hasConnections, "Test5")
		assert(element.endpoints.filterHead((x)=>x.region == "SG").get.hasConnections, "Test6")
		assert(element.endpoints.filterHead((x)=>x.region == "HK").get.connections.length == 2, "Test5")
		assert(element.endpoints.filterHead((x)=>x.region == "SG").get.connections.length == 2, "Test7")
	}

	test("Load Dependencies by order") {
		val configuration: List[ConnectionConfigurationElement] = Configuration.getConnectionsByDependencies
		val CSPider_SimpleEndpointsIndex: Int = configuration.indexWhere(x=>x.name == "CSPider_SimpleEndpoints")
		val CSPider_HK: Int = configuration.indexWhere(x=>x.name == "CSPider_HK")
		val CSPider_SG: Int = configuration.indexWhere(x=>x.name == "CSPider_SG")
		val AGCDB: Int = configuration.indexWhere(x=>x.name == "AGCDB")
		assert(AGCDB > CSPider_SimpleEndpointsIndex, "Test1")
		assert(AGCDB > CSPider_HK, "Test2")
		assert(AGCDB > CSPider_SG, "Test3")
	}

	private def getConnectionByName(connectionName: String) : ConnectionConfigurationElement = {
		val configuration: List[ConnectionConfigurationElement] = Configuration.getConnections
		val element: ConnectionConfigurationElement = configuration.filterHead((x)=>x.name == connectionName).get
		element
	}
}
