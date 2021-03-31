package dsl

import org.slf4j.LoggerFactory
import zio.{Has, Layer, UIO, ZIO, ZLayer}

package object logger {

  type Logger = Has[Logger.Service]

  object Logger {

    trait Service {
      def info(msg: String): UIO[Unit]
    }

    def info(msg: String): ZIO[Logger, Nothing, Unit] =
      ZIO.accessM(_.get.info(msg))

    val live: Layer[Nothing, Logger] =
      ZLayer.succeed(Service.live)

    object Service {
      val live: Service = new Service {
        private val logger = LoggerFactory.getLogger(this.getClass())

        def info(msg: String): UIO[Unit] =
          UIO.effectTotal(logger.info(msg))
      }
    }
  }
}
