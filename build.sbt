name := "aws-lambda-graal-native-scala-example"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val fixAll = taskKey[Unit]("Run scalafix / scalafmt")
lazy val deploy = taskKey[Unit]("Deploy to lambda")

lazy val commonSettings = Seq(
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.13.5",
  scalacOptions ++= List(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Yrangepos",
    "-Ymacro-annotations",
    "-Ywarn-unused",
    "-Xlint",
    "-Xfatal-warnings"
  ),
  // scalafix
  addCompilerPlugin(scalafixSemanticdb),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val root = (project in file("."))
  .enablePlugins(NativeImagePlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= {
      val circeVersion = "0.14.1"
      Seq(
        "io.circe"                      %% "circe-core"      % circeVersion,
        "io.circe"                      %% "circe-generic"   % circeVersion,
        "io.circe"                      %% "circe-parser"    % circeVersion,
        "dev.zio"                       %% "zio"             % "1.0.10",
        "com.softwaremill.sttp.client3" %% "core"            % "3.2.3",
        "ch.qos.logback"                 % "logback-classic" % "1.2.5",
        "org.scalatest"                 %% "scalatest"       % "3.2.10" % "test"
      )
    }
  )
  .settings(
    nativeImageOutput := file("serverless") / "dist" / "bootstrap",
    nativeImageOptions ++= List(
      "-H:+ReportExceptionStackTraces",
      "-H:IncludeResources=.*\\.properties",
      "-H:ReflectionConfigurationFiles=" + baseDirectory.value / "graal" / "reflect-config.json",
      "-H:EnableURLProtocols=http,https",
      "-H:+TraceClassInitialization",
      "--initialize-at-build-time=scala.runtime.Statics$VM,scala,ch.qos,org.slf4j,jdk,javax,org.apache,com.sun",
      "--no-fallback",
      "--no-server",
      "--allow-incomplete-classpath"
    )
  )
  .settings(
    fixAll :=
      Def
        .sequential(
          (Compile / scalafix).toTask(""),
          (Test / scalafix).toTask(""),
          (Compile / scalafmt),
          (Test / scalafmt)
        )
        .value,
    deploy := Serverless.deploy
  )

addCommandAlias("dist", "clean; fixAll; test; nativeImage")
