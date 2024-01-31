package com.harana.sbt.js

import com.harana.sbt.common._
import org.scalajs.sbtplugin.ScalaJSPlugin
import sbt.Keys._
import sbt._
import sbtghpackages.GitHubPackagesKeys.githubRepository
import org.scalablytyped.converter.plugin._
import org.scalablytyped.converter.plugin.STKeys._
import org.scalablytyped.converter.plugin.ScalablyTypedPluginBase.autoImport._

import scala.sys.process._

object Plugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    def haranaProject(id: String): Project =
      Project(id = id, file(id))
        .enablePlugins(ScalaJSPlugin, ScalablyTypedConverterExternalNpmPlugin)
        .settings(
          organization := "com.harana",
          name := id,
          githubRepository := id,
          Library.compilerPlugins,
          Settings.common,
          Settings.js,
          libraryDependencies ++= Library.common.value,
          libraryDependencies ++=  Library.js.value,
          libraryDependencySchemes ++= Library.jsLibraryDependencySchemes.value,
          dependencyOverrides := Library.jsOverrides.value,
          unmanagedBase := (ThisBuild / baseDirectory).value / "lib"
        )
  }
}