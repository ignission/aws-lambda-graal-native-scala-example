import scala.sys.process._
import java.io.File
import sbt.Keys._

class Logger {
  val out = scala.collection.mutable.ArrayBuffer[String]()
  val err = scala.collection.mutable.ArrayBuffer[String]()
  val log = ProcessLogger((o: String) => out += o, (e: String) => err += e)

  def print(): Unit = {
    println(out.toList.mkString("\n"))
    println(err.toList.mkString("\n"))
  }

  def flush(): Unit = {
    out.clear()
    err.clear()
  }
}

object Serverless {
  def deploy: Unit = {
    val logger        = new Logger
    val distDir       = new File("serverless/dist")
    val serverlessDir = new File("serverless")

    Process("chmod 755 bootstrap", distDir) ! logger.log
    Process("zip lambda.zip bootstrap", distDir) ! logger.log
    Process("yarn", serverlessDir) ! logger.log
    logger.print()
    logger.flush()

    Process("./node_modules/serverless/bin/serverless.js deploy", serverlessDir) ! logger.log
    logger.print()
  }
}
