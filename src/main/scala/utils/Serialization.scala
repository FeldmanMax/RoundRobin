package utils

import io.circe.{Decoder, Encoder, Json}
import io.circe.syntax._

object Serialization {
	def encode[A](data: A)(implicit m: Encoder[A]): Either[String, Json] = try {
		Right(data.asJson)
	}
	catch {
		case ex: Exception => Left(ex.getMessage)
	}

	def decode[A <: AnyRef](data: String)(implicit d: Decoder[A]): Either[String, A] = try {
		io.circe.parser.decode[A](data) match {
			case Left(error) => Left(error.getMessage)
			case Right(instance) => Right(instance)
		}
	}
	catch {
		case ex: Exception => Left(ex.toString)
	}
}