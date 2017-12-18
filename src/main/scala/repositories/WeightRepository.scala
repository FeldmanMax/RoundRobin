package repositories

import models.Weight

trait WeightRepository {

  def get(name: String, default: Option[Weight] = None): Either[String, Weight]
  def updateWeight(weightName: String, weight: Weight): Either[String, Weight]
}
