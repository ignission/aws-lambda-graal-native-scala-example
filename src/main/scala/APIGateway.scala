package example

case class APIGatewayRequest(body: String, path: String)

case class APIGatewayResponse(
    statusCode: Int = 200,
    headers: Map[String, String] = Map("Content-Type" -> "application/json"),
    body: String,
    isBase64Encoded: Boolean = false
)

object APIGatewayResponse {
  def success(body: String): APIGatewayResponse =
    APIGatewayResponse(body = body)
  def fail(body: String, statusCode: Int): APIGatewayResponse =
    APIGatewayResponse(body = body, statusCode = statusCode)
}
