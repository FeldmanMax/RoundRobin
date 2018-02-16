package repositories

import roundrobin.models.api.EndpointWeight

trait WeightRepository {

  def get(name: String, default: Option[EndpointWeight] = None): Either[String, EndpointWeight]
  def updateWeight(weightName: String, weight: EndpointWeight): Either[String, EndpointWeight]
}
