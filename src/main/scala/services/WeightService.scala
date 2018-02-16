package services

import javax.inject.Named

import com.google.inject.Inject
import models.internal.{Connection, Point}
import repositories.WeightRepository
import roundrobin.models.api.{EndpointWeight, WeightRate}
import utils.{Consts, PointsCalculator}

class WeightService @Inject() (@Named("points_service") 		val pointsService: PointsService,
															 @Named("weight_repository") 	val weightRepository: WeightRepository){

	def create(name: String): EndpointWeight = EndpointWeight(name, pointsService.getPoints(Consts.POINTS_MAX_AMOUNT))
	def updateWeight(weight: EndpointWeight, weightRate: WeightRate): Either[String, EndpointWeight] = {
		val newWeight: EndpointWeight = weight.copy(points = pointsService.update(weight.points,
																																			weightRate.isSuccess,
																																			weightRate.isPercent,
																																			weightRate.quantity))
    weightRepository.updateWeight(newWeight.endPointName, newWeight)
	}

	def updateWeight(weightName: String, weightRate: WeightRate): Either[String, EndpointWeight] = {
		(weightRepository.get(weightName) match {
			case Left(_) => weightRepository.updateWeight(weightName, create(weightName))
			case Right(w) => Right(w)
		}).right.flatMap { weight =>
			updateWeight(weight, weightRate)
		}
  }

	def next(groups: List[EndpointWeight]): EndpointWeight = nextImpl(groups, pointsService.getPoint)
	def getOrDefault(name: String, default: Option[EndpointWeight] = None): Either[String, EndpointWeight] = weightRepository.get(name, default)
	def getConnectionWeight(connection: Connection): List[EndpointWeight] = {
		val list = connection.endpointNames
			.map { endpointName => getOrDefault(endpointName, Option(create(endpointName)))}
			.filter(x=>x.isRight)
			.map(x=>x.right.get)
		list
	}

	private def nextImpl(groups: List[EndpointWeight], point: Point): EndpointWeight = PointsCalculator.minDistance(point, groups)
}
