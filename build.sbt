scalaVersion in ThisBuild := "2.12.0"

name in ThisBuild := "color"

organization in ThisBuild := "ai.dragonfly"

version in ThisBuild := "0.1"

publishTo in ThisBuild := Some(Resolver.file("file",  new File( "/var/www/maven" )) )

val color = crossProject.settings(
  libraryDependencies += "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"
).jsSettings(
  // JS-specific settings here
).jvmSettings(
  // JVM-specific settings here
)

lazy val js = color.js

lazy val jvm = color.jvm
