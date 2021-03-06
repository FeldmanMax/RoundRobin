package serialization

import io.circe.Decoder.Result
import models.KeyValue
import models.internal._
import serialization.KeyValueSerializer._
import utils.InternalTypes.KeyJsonValuePair

object ConnectionSerializer {
  import io.circe._
  import io.circe.syntax._

  implicit val encodeConnection: Encoder[Connection] = new Encoder[Connection] {
    override def apply(a: Connection): Json = {
      val members = List[KeyJsonValuePair](
        ("info", a.info.asJson),
        ("endpointsList", a.endpointsList.asJson),
        ("metadata", a.metadata.list.asJson)
      )
      Json.obj(members: _ *)
    }
  }

  implicit val decodeConnectionList: Decoder[List[Connection]] = new Decoder[List[Connection]] {
    override def apply(c: HCursor): Result[List[Connection]] = {
      c.downField("connections").values match {
        case None => Left(DecodingFailure.apply("Config file is empty", List.empty))
        case Some(array) =>
          val list: List[Decoder.Result[Connection]] = array.toList.map { connectionJson => connectionJson.as[Connection] }
          if(list.exists(decoder => decoder.isLeft))  {
            Left(DecodingFailure.apply(list.filter(x=>x.isLeft).map(x=>x.left.get).map(x=>x.message) mkString "\n", List.empty))
          }
          else Right(list.map(x=>x.right.get))
      }
    }
  }

  implicit val encoderConnectionList: Encoder[List[Connection]] = new Encoder[List[Connection]] {
    override def apply(info: List[Connection]): Json = {
      Json.obj(List[KeyJsonValuePair](("connections", info.map(_.asJson).asJson)): _ *)
    }
  }

  implicit val decodeConnection: Decoder[Connection] = new Decoder[Connection] {
    override def apply(c: HCursor): Result[Connection] = {
      for {
        info <- c.downField("info").as[ConnectionGeneralInfo]
        endpointsList <- c.downField("endpointsList").as[List[ConnectionEndpoint]]
      } yield Connection(info, endpointsList, handleMetadata(c))
    }
  }

  // Metadata is an OPTIONAL configuration.
  private def handleMetadata(c: HCursor): ConnectionMetadata[String] = {
    try {
      ConnectionMetadata[String](c.downField("metadata").as[List[KeyValue[String]]].getOrElse(List.empty[KeyValue[String]]))
    } catch {
      case _: Exception => ConnectionMetadata[String](List.empty[KeyValue[String]])
    }
  }

  implicit val encodeConnectionGeneralInfo: Encoder[ConnectionGeneralInfo] = new Encoder[ConnectionGeneralInfo] {
    override def apply(info: ConnectionGeneralInfo): Json = {
      val members: List[KeyJsonValuePair] = List (
        ("name", Json.fromString(info.name)),
        ("isUsingConnections", Json.fromBoolean(info.isUsingConnections)),
        ("is_active", Json.fromBoolean(info.is_active))
      )
      Json.obj(members: _ *)
    }
  }

  implicit val decodeConnectionGeneralInfo: Decoder[ConnectionGeneralInfo] = new Decoder[ConnectionGeneralInfo] {
    final def apply(c: HCursor): Decoder.Result[ConnectionGeneralInfo] = {
      for {
        name <- c.downField("name").as[String]
        isUsingConnections <- c.downField("isUsingConnections").as[Boolean]
        is_active <- c.downField("is_active").as[Boolean]
      } yield ConnectionGeneralInfo(name, isUsingConnections, is_active)
    }
  }

  implicit val encodeConnectionEndpoint: Encoder[ConnectionEndpoint] = new Encoder[ConnectionEndpoint] {
    override def apply(info: ConnectionEndpoint): Json = {
      val members: List[KeyJsonValuePair] = List (
        ("name", Json.fromString(info.info.key)),
        ("value", Json.fromString(info.info.value)),
        ("is_active", Json.fromBoolean(info.is_active))
      )
      Json.obj(members: _ *)
    }
  }

  implicit val decodeConnectionEndpoint: Decoder[ConnectionEndpoint] = new Decoder[ConnectionEndpoint] {
    final def apply(c: HCursor): Decoder.Result[ConnectionEndpoint] = {
      for {
        name <- c.downField("name").as[String]
        value <- c.downField("value").as[String]
        is_active <- c.downField("is_active").as[Boolean]
      } yield ConnectionEndpoint(KeyValue[String](name, value), is_active)
    }
  }
}
