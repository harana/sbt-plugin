package com.harana.sbt.js_jvm

import java.nio.charset.Charset
import java.nio.file._
import com.harana.sbt.common._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalPlugin
import sbt.Keys._
import sbt.{Def, _}
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport.scalaJSUseMainModuleInitializer
import sbtcrossproject.CrossPlugin.autoImport._
import sbtcrossproject.CrossProject
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import sbtghpackages.GitHubPackagesKeys.githubRepository
import scala.sys.process._

object Plugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {

    val utf8: Charset = Charset.forName("UTF-8")
    val fastCompile = TaskKey[Unit]("fastCompile")
    val fullCompile = TaskKey[Unit]("fullCompile")

    def haranaRootProject(crossProject: CrossProject): Project =
      Project("root", file(".")).
        aggregate(crossProject.js, crossProject.jvm).
        settings(
          Settings.common,
          publish         := {},
          publishLocal    := {},
          publishArtifact := false
        )

    def haranaCrossProject(id: String): CrossProject =
      CrossProject(id = id, file(id))(JSPlatform, JVMPlatform)
        .crossType(CrossType.Full)
        .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin))
        .jvmConfigure(_.enablePlugins(JavaAppPackaging, UniversalPlugin))
        .settings(
          organization := "com.harana",
          name := id,
          githubRepository := id,
          Library.compilerPlugins,
          libraryDependencies ++= Library.common.value,
          Settings.common,
          unmanagedBase := (ThisBuild / baseDirectory).value / "lib"
        )
        .jsSettings(
          name := id,
          Library.compilerPlugins,
          dependencyOverrides ++= Library.globalDependencyOverrides.value,
          fastCompile := { compileJS(baseDirectory).dependsOn(Compile / fastOptJS / webpack) }.value,
          fullCompile := { compileJS(baseDirectory).dependsOn(Compile / fullOptJS / webpack) }.value,
          libraryDependencies ++=  Library.js.value,
          scalaJSUseMainModuleInitializer := false,
          Settings.common,
          Settings.js,
          unmanagedBase := (ThisBuild / baseDirectory).value / "lib"
        )
        .jvmSettings(
          name := id,
          Library.compilerPlugins,
          dependencyOverrides ++= Library.globalDependencyOverrides.value,
          excludeDependencies ++= Library.globalExclusions.value,
          libraryDependencies ++= Library.jvm.value,
          Settings.common,
          Settings.jvm,
          unmanagedBase := (ThisBuild / baseDirectory).value / "lib"
        )

    def compileJS(baseDirectory: SettingKey[File]) = {
      baseDirectory.map { base =>

        val nodeModules = new File(base, "target/scala-2.12/scalajs-bundler/main/node_modules").list().toList
        if (!nodeModules.contains("webpack-merge"))
          s"yarn add ml-matrix @nivo/waffle webpack-merge ${base.absolutePath}/target/scala-2.12/scalajs-bundler/main/node_modules" !

        new File(base, "target/scala-2.12/scalajs-bundler/main").listFiles((dir, name) => name.toLowerCase.contains("opt"))
          .foreach(
            file => Files.copy(file.toPath, new File(base, s"../jvm/src/main/resources/public/js/${file.getName}").toPath, StandardCopyOption.REPLACE_EXISTING)
          )
      }
    }
  }
}