package com.harana.sbt.js

import com.harana.sbt.common._
import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._
import sbtghpackages.GitHubPackagesKeys.githubRepository

import scala.sys.process._

object Plugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    def haranaProject(id: String): Project =
      Project(id = id, file(id))
        .enablePlugins(ScalaJSPlugin)
        .settings(
          organization := "com.harana",
          name := id,
          githubRepository := id,
          Library.compilerPlugins,
          Settings.common,
          Settings.js,
          libraryDependencies ++= Library.common.value,
          libraryDependencies ++=  Library.js.value,
          dependencyOverrides := Library.jsOverrides.value,
        )
  }
}