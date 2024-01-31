package com.harana.sbt.common

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import sbtghpackages._
import sbtghpackages.GitHubPackagesKeys._
import sbt.Keys.{baseDirectory, _}
import sbt._
import sbt.nio.Keys.{ReloadOnSourceChanges, onChangedBuildSource}
import sbt.{File, nio, _}
import org.scalablytyped.converter.plugin.STKeys._
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase.autoImport._

import scala.sys.process._
import org.scalajs.linker.interface.Report

object Settings {

  lazy val buildFrontend = taskKey[Unit]("")
  lazy val buildJs = taskKey[Map[String, File]]("")
  lazy val buildCss = taskKey[Unit]("")
  lazy val frontendReport = taskKey[(Report, File)]("")
  lazy val isRelease = sys.env.get("RELEASE").contains("true")

  def common = Seq(
    scalaVersion                              := "2.13.12",
    scalacOptions                             ++= Seq(
                                                      "-deprecation", "-feature", "-unchecked", "-language:higherKinds", "-language:implicitConversions",
                                                      "-language:postfixOps", "-Yrangepos", "-Ybackend-parallelism", "8", "-Ybackend-worker-queue", "8",
                                                      "-language:experimental.macros", "-Ymacro-annotations"
                                                    ),
    doc / sources                                 := Seq(),
    packageDoc / publishArtifact                  := false,
    publishArtifact in (Compile, packageDoc)      := false,
    packageSrc / publishArtifact                  := false,
    publishArtifact in (Compile, packageSrc)      := false,

    githubOwner                                   := "harana",
    organization                                  := "com.harana",
    githubTokenSource                             := TokenSource.Environment("GITHUB_TOKEN"),

    updateOptions                                 := updateOptions.value.withCachedResolution(true),
    testFrameworks                                := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    crossPaths                                    := true,
    resolvers                                     ++= Library.resolvers,
    dependencyOverrides                           ++= Library.globalDependencyOverrides.value,
    updateOptions                                 := updateOptions.value.withCachedResolution(true),
    Test / parallelExecution                      := false,
    Global / nio.Keys.onChangedBuildSource        := ReloadOnSourceChanges
  )

  val js = Seq(
		scalaJSUseMainModuleInitializer           := false,
    Global / onChangedBuildSource             := ReloadOnSourceChanges,
    scalaJSLinkerConfig                       ~= (_.withModuleKind(ModuleKind.ESModule)),
    unmanagedBase                             := (ThisProject / unmanagedBase).value,
    externalNpm                               := {
                                                  sys.process.Process(Seq("pnpm", "--silent", "--cwd", baseDirectory.value.toString)).!
                                                  baseDirectory.value
                                                 },
    stIgnore ++= List(
      "@headlessui/react",
      "@heroicons/react",
      "@lit/reactive-element",
      "@mapbox/togeojson",
      "@niivue/niivue",
      "@shoelace-style/shoelace",
      "@tailwindcss/aspect-ratio",
      "@tailwindcss/forms",
      "@tailwindcss/typography",
      "@xeokit/xeokit-sdk",
      "history",
      "katex",
      "keen-slider",
      "leaflet",
      "meshgrad",
      "notebookjs",
      "prismjs",
      "react-json-lens",
      "react-leaflet-kml",
      "react-proxy",
      "react-virtuoso",
      "styled-components",
      "tailwindcss",
      "tauri-plugin-log-api",
      "type-fest",
      "vite",
      "xlsx-viewer"
    ),
    stFlavour := Flavour.Slinky,
    stIncludeDev := true,
    stMinimize := Selection.AllExcept("@tauri-apps/api"),
    stOutputPackage := "com.harana.js",
    stQuiet := true,
    Compile / packageSrc / mappings ++= {
      val base = (Compile / sourceManaged).value
      val files = (Compile / managedSources).value
      files.map { f => (f, f.relativeTo(base).get.getPath) }
    },
    frontendReport := {
      if (isRelease) (Compile / fullLinkJS).value.data -> (Compile / fullLinkJS / scalaJSLinkerOutputDirectory).value
      else (Compile / fastLinkJS).value.data -> (Compile / fastLinkJS / scalaJSLinkerOutputDirectory).value
    },
    buildCss := {
      s"pnpm run --dir ${baseDirectory.value.toString} css" !
    },
    buildFrontend := {
      val (report, fm) = frontendReport.value
      IO.listFiles(fm).toList.map { file =>
        val (name, ext) = file.baseAndExt
        val out = baseDirectory.value / "target" / (name + "." + ext)
        IO.copyFile(file, out)
        file.name -> out
      }.toMap
    },
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
    unmanagedBase                             := (ThisProject / unmanagedBase).value,
    run / fork                                := true,
    javacOptions                              ++= Seq("-encoding", "UTF-8"),
    javaOptions                               ++= javaLaunchOptions,
    Universal / javaOptions                   ++= javaLaunchOptions.map(lo => s"-J$lo"),
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
                                                  OsgiStage / libraryDependencies := Library.osgi.value,
                                                  libraryDependencies ++= Library.osgi.value,
                                                  copyOSGIBundlesTask := {
                                                    (OsgiStage / update).value.allFiles.foreach { f =>
                                                      (OsgiStage / libraryDependencies).value.foreach { lib =>
                                                        (Compile / dependencyClasspath).value.foreach { dep =>
                                                          if (dep.metadata.get(moduleID.key).get.equals(lib) && !dep.data.getName.contains("felix.framework")) {
                                                            IO.copyFile(dep.data, new File("bundles",dep.data.getName))
                                                          }
                                                        }
                                                      }
                                                    }
                                                  },
    Compile / compile                         := ((Compile / compile) dependsOn copyOSGIBundlesTask).value,
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
