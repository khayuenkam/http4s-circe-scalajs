import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val commonSettings = Seq(
  organization := "com.github.khayuenkam",
  scalaVersion := "2.12.8"
)

val http4sVersion = "0.20.0-RC1"
val circeVersion = "0.10.0"
val logbackVersion = "1.2.3"

lazy val shared = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared"))
  .settings(commonSettings)
  .settings(
    name := "http4s-circe-scalajs-example-shared"
  )
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  )

lazy val jvm = (project in file("jvm"))
  .settings(commonSettings)
  .settings(
    name := "http4s-circe-scalajs-example-jvm"
  )
  .settings(
    libraryDependencies ++= Seq(
      "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"     %% "http4s-circe"        % http4sVersion,
      "org.http4s"     %% "http4s-dsl"          % http4sVersion,
      "ch.qos.logback" % "logback-classic"      % logbackVersion
    ),
    resources in Compile += (fastOptJS in (js, Compile)).value.data,
    resources in Compile += (fastOptJS in (js, Compile)).value
      .map((x: sbt.File) => new File(x.getAbsolutePath + ".map"))
      .data,
  )
  .dependsOn(shared.jvm)

lazy val js = (project in file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings)
  .settings(
    name := "http4s-circe-scalajs-example-js",
    scalaJSUseMainModuleInitializer := true
  )
  .settings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2"
  )
  .dependsOn(shared.js)
