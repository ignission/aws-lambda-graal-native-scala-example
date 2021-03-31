package dsl

import sttp.client3._
import sttp.model.Header
import zio.{Has, Layer, ZIO, ZLayer}

import scala.util.Try

case class HttpResponse(body: String, headers: Map[String, String])

object HttpResponse {
  def from(body: String, headers: Seq[Header]): HttpResponse =
    HttpResponse(
      body = body,
      headers = headers.map(h => (h.name -> h.value)).toMap
    )
}

sealed trait HttpError extends RuntimeException
case class BadRequest(text: String) extends HttpError {
  override def getMessage(): String = s"Http bad request. $text"
}
case class ServerError(text: String) extends HttpError {
  override def getMessage(): String = s"Http Server error. $text"
}

package object http {

  type Http = Has[Http.Service]

  object Http {

    trait Service {
      def get(url: String): Either[HttpError, HttpResponse]
      def post(
          url: String,
          headers: Map[String, String],
          body: String
      ): Either[HttpError, HttpResponse]
    }

    def get(url: String): ZIO[Http, HttpError, HttpResponse] =
      ZIO.accessM(env => ZIO.fromEither(env.get.get(url)))

    def post(
        url: String,
        headers: Map[String, String],
        body: String
    ): ZIO[Http, HttpError, HttpResponse] =
      ZIO.accessM(env => ZIO.fromEither(env.get.post(url, headers, body)))

    val live: Layer[Nothing, Http] =
      ZLayer.succeed(Service.live)

    object Service {
      val live: Service = new Service {

        private val backend = HttpURLConnectionBackend()

        def get(url: String): Either[HttpError, HttpResponse] =
          doRequest(basicRequest.get(uri"$url"))

        def post(
            url: String,
            headers: Map[String, String],
            body: String
        ): Either[HttpError, HttpResponse] =
          doRequest(basicRequest.body(body).post(uri"$url"))

        private def doRequest(
            request: Request[Either[String, String], Any]
        ): Either[HttpError, HttpResponse] =
          for {
            response <- Try(request.send(backend))
              .map(Right(_))
              .recover {
                case e: SttpClientException =>
                  Left(ServerError(e.getMessage()))
                case e =>
                  throw e
              }
              .get
            result <- response.body match {
              case Right(value) =>
                Right(HttpResponse.from(value, response.headers))
              case Left(error) if response.code.isClientError =>
                Left(BadRequest(error))
              case Left(error) =>
                Left(ServerError(error))
            }
          } yield result
      }
    }
  }
}
