package example

case class APIGatewayRequest(body: String, path: String)

case class APIGatewayResponse(
    statusCode: Int = 200,
    headers: Map[String, String] = Map(),
    body: String,
    isBase64Encoded: Boolean = false
)
