package com.harana.sbt.common

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import sbtghpackages._
import sbtghpackages.GitHubPackagesKeys._
import sbt.Keys.{baseDirectory, _}
import sbt._

object Settings {

  def common = Seq(
    scalaVersion                              := "2.12.18",
    scalacOptions                             ++= Seq(
                                                      "-deprecation", "-feature", "-unchecked", "-language:higherKinds", "-language:implicitConversions",
                                                      "-language:postfixOps", "-Xfuture", "-Ypartial-unification", "-Yrangepos", "-Ybackend-parallelism", "8",
                                                      "-Ybackend-worker-queue", "8"
                                                    ),
    sources in (doc)                          := Seq(),
    publishArtifact in packageDoc             := false,
    publishArtifact in (Compile, packageDoc)  := false,
    publishArtifact in packageSrc             := false,
    publishArtifact in (Compile, packageSrc)  := false,

    githubOwner                               := "harana",
    organization                              := "com.harana",
    githubTokenSource                         := TokenSource.Environment("GITHUB_TOKEN"),

    updateOptions                             := updateOptions.value.withCachedResolution(true),
    testFrameworks                            := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )

  val js = Seq(
		scalaJSUseMainModuleInitializer           := true,
    scalaJSLinkerConfig                       ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
		useYarn                                   := true,
		version in webpack                        := "4.46.0",
		version in startWebpackDevServer          := "3.1.4",
		webpackDevServerExtraArgs in fastOptJS    := Seq("--inline", "--hot"),
		webpackEmitSourceMaps                     := false,
		webpackBundlingMode in fastOptJS          := BundlingMode.LibraryOnly(),
		webpackBundlingMode in fullOptJS          := BundlingMode.Application,
		webpackConfigFile in fastOptJS            := Some(baseDirectory.value / "webpack-dev.js"),
		webpackConfigFile in fullOptJS            := Some(baseDirectory.value / "webpack-prod.js"), 
    unmanagedBase                             := (unmanagedBase in ThisProject).value
  )

  val javaLaunchOptions                        = if (javaVersion > 9) Seq(
                                                      "--add-modules=jdk.incubator.foreign",
                                                      "--add-opens=java.base/java.io=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.lang=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.math=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.net=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.security=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.text=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.util.concurrent.atomic=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.util.concurrent.locks=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.util.concurrent=ALL-UNNAMED",
                                                      "--add-opens=java.base/java.util=ALL-UNNAMED",
                                                      "--add-opens=java.base/jdk.internal.reflect=ALL-UNNAMED",
                                                      "--add-opens=java.base/sun.security.pkcs=ALL-UNNAMED",
                                                      "--add-opens=java.base/sun.security.rsa=ALL-UNNAMED",
                                                      "--add-opens=java.base/sun.security.x509=ALL-UNNAMED",
                                                      "--add-opens=java.management/javax.management.openmbean=ALL-UNNAMED",
                                                      "--add-opens=java.management/javax.management=ALL-UNNAMED",
                                                      "--add-opens=java.naming/javax.naming=ALL-UNNAMED",
                                                      "--add-opens=java.sql/java.sql=ALL-UNNAMED"
                                                    ) else Seq()

  val jvm = Seq(
    unmanagedBase                             := (unmanagedBase in ThisProject).value,
    fork in run                               := true,
    javacOptions                              ++= Seq("-encoding", "UTF-8"),
    javaOptions                               ++= javaLaunchOptions,
    javaOptions in Universal                  ++= javaLaunchOptions.map(lo => s"-J$lo"),
    javaOptions                               ++= {
                                                    val Digits = "^(\\d+)$".r
                                                    sys.env.get("HARANA_DEBUG") match {
                                                      case Some("true") =>
                                                        Seq("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
                                                      case Some(Digits(port)) =>
                                                        Seq(s"-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=$port")
                                                      case _ =>
                                                        Seq.empty
                                                    }
                                                  }
  )

  val copyOSGIBundlesTask                     = taskKey[Unit]("Copy OSGI system bundles task")
  val OsgiStage                               = config("CopyOSGIBundlesTask")
  val osgi                                    = Seq(
                                                  libraryDependencies in OsgiStage := Dependencies.osgi.value,
                                                  libraryDependencies ++= Dependencies.osgi.value,
                                                  copyOSGIBundlesTask := {
                                                    (update in OsgiStage).value.allFiles.foreach { f =>
                                                      (libraryDependencies in OsgiStage).value.foreach { lib =>
                                                        (dependencyClasspath in Compile).value.foreach { dep =>
                                                          if (dep.metadata.get(moduleID.key).get.equals(lib) && !dep.data.getName.contains("felix.framework")) {
                                                            IO.copyFile(dep.data, new File("bundles",dep.data.getName))
                                                          }
                                                        }
                                                      }
                                                    }
                                                  },
    compile in Compile                        := ((compile in Compile) dependsOn copyOSGIBundlesTask).value,
    //mappings in Universal                   ++= directory(baseDirectory.value / "bundles"),
    //mappings in Universal                   ++= directory(baseDirectory.value / "plugins"),
  ) ++ inConfig(OsgiStage)(Classpaths.ivyBaseSettings)

  def ramDisk(baseDirectory: File, name: String, size: Long) = {
    val cmd = s"""(rm -rf $baseDirectory/target) &&
      | (mount | grep -q /Volumes/$name || diskutil apfs create `hdiutil attach -nomount ram://$size` $name) &&
      | (ln -s /Volumes/$name $baseDirectory/target)
    """.stripMargin
    Seq("bash", "-c", cmd)
  }

  def javaVersion = {
    var version = System.getProperty("java.version")
    if (version.startsWith("1.")) version = version.substring(2, 3)
    else {
      val dot = version.indexOf(".")
      if (dot != -1) version = version.substring(0, dot)
    }
    val dash = version.indexOf("-")
    if (dash != -1) version = version.substring(0, dash)
    version.toInt
  }
}
