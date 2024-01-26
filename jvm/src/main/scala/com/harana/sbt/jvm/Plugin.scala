package com.harana.sbt.jvm

import com.harana.sbt.common._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalPlugin
import sbt.Keys._
import sbt._
import sbtghpackages.GitHubPackagesKeys.githubRepository

object Plugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    def haranaProject(id: String): Project =
      Project(id = id, file(id))
        .enablePlugins(JavaAppPackaging, UniversalPlugin)
        .settings(
          organization := "com.harana",
          name := id,
          githubRepository := id,
          Dependencies.compilerPlugins,
          Settings.common,
          Settings.jvm,
          ThirdPartyResolvers.all,
          unmanagedBase := (baseDirectory in ThisBuild).value / "lib",
          libraryDependencies ++= Dependencies.common.value,
          libraryDependencies ++= Dependencies.jvm.value,
          dependencyOverrides ++= Dependencies.jvmOverrides.value,
          excludeDependencies ++= Dependencies.jvmExcludes.value
        )
  }
}