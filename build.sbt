ThisBuild / scalaVersion := "3.1.0"
ThisBuild / publishTo := Some( Resolver.file( "file",  new File("/var/www/maven") ) )

lazy val color = crossProject(JSPlatform, JVMPlatform).settings(
  publishTo := Some(Resolver.file("file",  new File( "/var/www/maven" ))),
  name := "color",
  version := "0.3",
  organization := "ai.dragonfly.code",
  resolvers += "dragonfly.ai" at "https://code.dragonfly.ai/",
  libraryDependencies += "ai.dragonfly.code" %%% "vector" % "0.4521",
  scalacOptions ++= Seq("-feature", "-deprecation"),
  Compile / mainClass := Some("ai.dragonfly.color.experiments.TestColors")
).jvmSettings().jsSettings(
  scalaJSUseMainModuleInitializer := true
)
