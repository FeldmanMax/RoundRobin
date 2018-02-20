package serialization

import io.circe.Decoder.Result
import models.internal.KeyValue
import utils.InternalTypes.KeyJsonValuePair
import io.circe._

object KeyValueSerializer {

  implicit val encodeKeyStringValue: Encoder[KeyValue[String]] = new Encoder[KeyValue[String]] {
    override def apply(a: KeyValue[String]): Json = {
      val members: List[KeyJsonValuePair] = List[KeyJsonValuePair](
        ("key", Json.fromString("key")),
        ("value", Json.fromString("value"))
      )
      Json.obj(members: _ *)
    }
  }

  implicit val decodeKeyStringValue: Decoder[KeyValue[String]] = new Decoder[KeyValue[String]] {
    override def apply(c: HCursor): Result[KeyValue[String]] = {
      for {
        key <- c.downField("key").as[String]
        value <- c.downField("value").as[String]
      } yield KeyValue[String](key, value)
    }
  }
}
