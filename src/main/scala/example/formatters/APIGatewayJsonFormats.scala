package example.formatters

import example.APIGatewayResponse
import io.circe.syntax._
import io.circe.{Encoder, Json}

object APIGatewayJsonFormats {

  implicit val responseEncoder: Encoder[APIGatewayResponse] =
    new Encoder[APIGatewayResponse] {
      override def apply(a: APIGatewayResponse): Json =
        Json.obj(
          ("statusCode", a.statusCode.asJson),
          ("headers", a.headers.asJson),
          ("body", a.body.asJson),
          ("isBase64Encoded", a.isBase64Encoded.asJson)
        )
    }
}
