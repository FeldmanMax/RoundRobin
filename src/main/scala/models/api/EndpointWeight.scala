package roundrobin.models.api

import models.internal.Point

case class EndpointWeight(endPointName: String, points: List[Point]) {
  def - (other: Point): Double = points.map(x=>x - other).min
  def size: Int = points.length
  override def toString: String = s"name: $endPointName, amount: ${points.size}"
}
