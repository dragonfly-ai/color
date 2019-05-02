import sbtcrossproject.CrossPlugin.autoImport.crossProject

val sharedSettings = Seq(
  version in ThisBuild := "0.2",
  scalaVersion := "2.12.6",
  organization in ThisBuild := "ai.dragonfly.code",
  publishTo in ThisBuild := Some(Resolver.file("file",  new File( "/var/www/maven" )) ),
  scalacOptions in ThisBuild ++= Seq("-feature"),
  resolvers in ThisBuild += "dragonfly.ai" at "http://code.dragonfly.ai:8080/",
  libraryDependencies += "ai.dragonfly.code" %%% "vector" % "0.2"
)

lazy val color = crossProject(JSPlatform, JVMPlatform).settings(sharedSettings)