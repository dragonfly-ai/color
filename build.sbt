ThisBuild / scalaVersion := "2.13.3"

lazy val root = project.in(file(".")).aggregate(color.js, color.jvm).settings(
  publishTo := Some( Resolver.file("file",  new File( "/var/www/maven" ) ) )
)

lazy val color = crossProject(JSPlatform, JVMPlatform).settings(
  publishTo := Some(Resolver.file("file",  new File( "/var/www/maven" )) ),
  name := "color",
  version := "0.2",
  organization := "ai.dragonfly.code",
  resolvers += "dragonfly.ai" at "https://code.dragonfly.ai:4343/",
  libraryDependencies += "ai.dragonfly.code" %%% "vector" % "0.2",
  scalacOptions ++= Seq("-feature"),
  mainClass in (Compile, run) := Some("ai.dragonfly.color.experiments.TestColors")
).jvmSettings().jsSettings(
  scalaJSUseMainModuleInitializer := true
)