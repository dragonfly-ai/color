scalaVersion in ThisBuild := "2.12.3"

name in ThisBuild := "color"

organization in ThisBuild := "ai.dragonfly.code"

version in ThisBuild := "0.1"

resolvers in ThisBuild += "dragonfly.ai" at "http://code.dragonfly.ai:8080/"

publishTo in ThisBuild := Some(Resolver.file("file",  new File( "/var/www/maven" )) )

val color = crossProject.settings(
  // shared settings
  libraryDependencies ++= Seq(
    "org.scala-js" %% "scalajs-dom_sjs0.6" % "0.9.1",
    "ai.dragonfly.code" %%% "vector" % "0.1"
  )
).jsSettings(
  // JS-specific settings here
).jvmSettings(
  // JVM-specific settings here
  libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
)

lazy val js = color.js

lazy val jvm = color.jvm
