package modules

import configuration.{Configuration, ConnectionConfigurationElement}
import data_modules.Angle
import utils.AnglesGenerator
import utils.Operators.OperatorsExtensions

case class ConnectionInformation(configurationElement: ConnectionConfigurationElement) {

	val name: String = configurationElement.name
	val region: ConnectionRegionInformation = {
		val regionToUse: String = configurationElement.region.nonEmpty ? (configurationElement.region, Configuration.connectionRegion)
		val currentRegion: String = Configuration.connectionRegion
		ConnectionRegionInformation(currentRegion, regionToUse)
	}

	var angles: List[Angle] = buildAngles

	private def buildAngles : List[Angle] = {
		val anglesGenerator: AnglesGenerator = new AnglesGenerator
		val overallAmount: Int = configurationElement.endpoints.count(x=>x.region == region.regionToUse)
		val angles: List[Angle] = anglesGenerator.generateAngles(configurationElement.connectionLimitations, overallAmount)
		angles
	}
}

case class ConnectionRegionInformation(currentRegion: String, regionToUse: String) {}