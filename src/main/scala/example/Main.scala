package example

import example.dsl.HttpResponse
import example.dsl.http.Http
import example.dsl.logger.Logger
import io.circe.Encoder
import io.circe.syntax._
import zio.{IO, UIO, ZIO}

object Main extends App {

  type AppType = Http with Logger

  private val layer               = Http.live ++ Logger.live
  private val runtime             = zio.Runtime.default
  private val awsLambdaRuntimeApi = System.getenv("AWS_LAMBDA_RUNTIME_API")

  while (true) {
    val program = for {
      request   <- getNextInvocation()
      _         <- Logger.info(s"Request body: ${request.body}")
      requestId <- getAwsRequestId(request)
      _         <- Logger.info(s"Request id: $requestId")
      result    <- execute("Hello, GraalVM native-image with Scala!")
      _         <- returnResponse(requestId, result)
    } yield result

    runtime.unsafeRun(
      program
        .provideLayer(layer)
        .fold(
          error => println("Error: " + error.toString()),
          value => println("Result: " + value.toString())
        )
    )
  }

  private def getNextInvocation(): ZIO[AppType, AppError, HttpResponse] =
    Http
      .get(s"http://${awsLambdaRuntimeApi}/2018-06-01/runtime/invocation/next")
      .mapError(e => AwsRequestFailed(e.getMessage()))

  private def getAwsRequestId(response: HttpResponse): IO[AppError, String] =
    IO.fromEither(
      response.headers
        .get("Lambda-Runtime-Aws-Request-Id")
        .map(Right(_))
        .getOrElse(Left(RequestIdNotDefined(response.headers)))
    )

  private def execute(message: String): ZIO[AppType, AppError, String] =
    // write some logic ...
    UIO.effectTotal(message)

  private def returnResponse[A](requestId: String, value: A)(implicit
      encoder: Encoder[A]
  ): ZIO[AppType, AppError, HttpResponse] = {
    import example.formatters.APIGatewayJsonFormats._

    val response = APIGatewayResponse.success(value.asJson.noSpaces)

    Http
      .post(
        s"http://${awsLambdaRuntimeApi}/2018-06-01/runtime/invocation/${requestId}/response",
        response.headers,
        response.asJson.noSpaces
      )
      .mapError(e => AwsResponseFailed(e.getMessage))
  }
}
