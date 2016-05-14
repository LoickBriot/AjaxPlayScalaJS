name := "spark"



import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin._
import sbt.Keys._
import sbt.Project.projectToRef





lazy val commonSettings = Seq(
  organization := "eu.tetrao",
  version := "0.1.1",
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-target:jvm-1.8")
)



lazy val shared = (crossProject.crossType(CrossType.Pure) in file("src/shared"))
  .settings(name := "shared")
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.3.1",
      "com.lihaoyi" %%% "upickle" % "0.4.0",
      "com.lihaoyi" %%% "autowire" % "0.2.5",
      "com.lihaoyi" %%% "scalarx" % "0.3.1",
      "me.chrons" %%% "boopickle" % "1.1.3"
    )
  )
  .jsConfigure(_ enablePlugins ScalaJSPlay)


lazy val sharedJVM = shared.jvm.settings(name := "sharedJVM")
lazy val sharedJS = shared.js.settings(name := "sharedJS")



lazy val client = (project in file("src/client"))
  .settings(name := "client")
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.0",
      "be.doeraene" %%% "scalajs-jquery" % "0.9.0",
      "com.lihaoyi" %%% "scalatags" % "0.5.2",
      "com.lihaoyi" %%% "scalarx" % "0.2.8",
      "org.singlespaced" %%% "scalajs-d3" % "0.3.3"
    )
  )
  .settings(jsDependencies += RuntimeDOM % "test")
  .settings(skip in packageJSDependencies := false)
  .settings(persistLauncher := true)
  .settings(persistLauncher in Test := false)
  .settings(testFrameworks += new TestFramework("utest.runner.Framework"))
  .settings(scalaJSUseRhino in Global := true)
  .settings(crossTarget in(Compile, fastOptJS) := new java.io.File("src/server/public/js"))
  .settings(crossTarget in(Compile, packageScalaJSLauncher) := new java.io.File("src/server/public/js"))
  .settings(crossTarget in(Compile, packageJSDependencies) := new java.io.File("src/server/public/js"))
  .settings(crossTarget in(Compile, fullOptJS) := new java.io.File("src/server/public/js"))
  .enablePlugins(ScalaJSPlugin, ScalaJSPlay)
  .dependsOn(sharedJS)





lazy val server  = (project in file("src/server"))
  .settings(name := "server")
  .settings(routesGenerator := InjectedRoutesGenerator)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.webjars" %% "webjars-play" % "2.4.0-2",
      "jp.t2v" %% "play2-auth" % "0.14.1",
      play.sbt.Play.autoImport.cache,
      "com.github.ancane" %% "hashids-scala" % "1.2",
      "com.vmunier" %% "play-scalajs-scripts" % "0.4.0",
      "org.webjars" % "jquery" % "1.11.1",
      "org.webjars.npm" % "foundation-sites" % "6.0.3",
      "org.webjars.npm" % "alertifyjs" % "1.4.1"
    )
  )
  .settings(scalaJSProjects := Seq(client))
  .settings(pipelineStages := Seq(scalaJSProd, gzip))
  .settings(resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases")
  .enablePlugins(PlayScala)
  .aggregate(Seq(client).map(projectToRef): _*)
  .dependsOn(sharedJVM)
