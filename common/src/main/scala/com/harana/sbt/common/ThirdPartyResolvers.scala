package com.harana.sbt.common

import sbt.Keys._
import sbt._
import sbtghpackages.GitHubPackagesPlugin.autoImport._

object ThirdPartyResolvers {

  val all = 
    resolvers ++= Seq(
      Resolver.mavenLocal,
      Resolver.githubPackages("harana"),
      Resolver.jcenterRepo,
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      Resolver.url("heroku-sbt-plugin-releases", url("https://dl.bintray.com/heroku/sbt-plugins/"))(Resolver.ivyStylePatterns),
      "Jitpack" at "https://jitpack.io",
      "Shibboleth" at "https://build.shibboleth.net/nexus/content/repositories/releases/",
      "Typesafe" at "https://repo.typesafe.com/typesafe/releases/",
      "spark-packages" at "https://dl.bintray.com/spark-packages/maven",
      "orientdb" at "https://dl.bintray.com/sbcd90/org.apache.spark",
      "mulesoft" at "https://repository.mulesoft.org/nexus/content/repositories/public/",
      "ossrh" at "https://oss.sonatype.org/service/local/staging/deploy/maven2",
      "exasol" at "https://maven.exasol.com/artifactory/exasol-releases/",
      "Airbyte" at "https://airbyte.mycloudrepo.io/public/repositories/airbyte-public-jars"
    )
}