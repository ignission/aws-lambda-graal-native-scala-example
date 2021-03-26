name := "aws-lambda-graal-native-scala-example"

addCommandAlias("fix", "all compile:scalafix; test:scalafix")
addCommandAlias("fixCheck", "; compile:scalafix --check; test:scalafix --check")
addCommandAlias("format", "; scalafmt; test:scalafmt; scalafmtSbt")
addCommandAlias("formatCheck", "; scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck")
addCommandAlias("fixAll", "fix; format")
addCommandAlias("checkAll", "fixCheck; formatCheck")

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.13.5",
  organization := "tech.ignission",
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
      val circeVersion = "0.13.0"
      Seq(
        "io.circe"      %% "circe-core"      % circeVersion,
        "io.circe"      %% "circe-generic"   % circeVersion,
        "io.circe"      %% "circe-parser"    % circeVersion,
        "dev.zio"       %% "zio"             % "1.0.5",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "org.scalatest" %% "scalatest"       % "3.2.3" % "test"
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
      "--initialize-at-build-time=scala.runtime.Statics$VM,scala,ch.qos,org.slf4j",
      "--no-fallback",
      "--no-server",
      "--allow-incomplete-classpath"
    )
  )
