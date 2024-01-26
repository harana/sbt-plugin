lazy val commonSettings = Seq(
    sbtPlugin                                 := true,
    organization                              := "com.harana",
    githubOwner                               := "harana",
    githubRepository                          := "sbt-plugin",
    githubTokenSource                         := TokenSource.Environment("GITHUB_TOKEN"),
    sources in (doc)                          := Seq(),
    Compile / packageDoc / publishArtifact    := false,
    Compile / packageSrc / publishArtifact    := false,
)

  lazy val root = Project("sbt-plugin", file("."))
    .settings(
      commonSettings,
      moduleName      := "root",
      publish         := {},
      publishLocal    := {},
      publishArtifact := false
    )
    .aggregate(common, js, jvm, js_jvm)

  lazy val common = project
    .settings(
      commonSettings,
      moduleName      := "sbt_common",
      name            := "sbt",
      resolvers += Resolver.sonatypeRepo("snapshots"),
      addSbtPlugin("org.portable-scala"           %   "sbt-scalajs-crossproject"    %   "1.1.0"),
      addSbtPlugin("org.scala-js"                 %   "sbt-scalajs"                 %   "1.7.1"),
      addSbtPlugin("ch.epfl.scala"                %   "sbt-scalajs-bundler"         %   "0.20.0"),
      addSbtPlugin("com.eed3si9n"                 %   "sbt-assembly"                %   "1.1.0"),
      addSbtPlugin("com.github.sbt"               %   "sbt-native-packager"         %   "1.9.2"),
      addSbtPlugin("com.timushev.sbt"             %   "sbt-updates"                 %   "0.5.3"),
      addSbtPlugin("com.codecommit"               %   "sbt-github-packages"         %   "0.5.3"),
      addSbtPlugin("com.rallyhealth.sbt"          %   "sbt-git-versioning"          %   "1.6.0")
    )
    .enablePlugins(SbtPlugin)
    .enablePlugins(SemVerPlugin)


  lazy val js = project
    .settings(
      commonSettings,
      moduleName      := "sbt_js",
      name            := "sbt"
    )
    .enablePlugins(SbtPlugin)
    .dependsOn(common)

  lazy val jvm = project
    .settings(
      commonSettings,
      moduleName      := "sbt_jvm",
      name            := "sbt"
    )
    .enablePlugins(SbtPlugin)
    .dependsOn(common)

  lazy val js_jvm = project
    .settings(
      commonSettings,
      moduleName      := "sbt_js_jvm",
      name            := "sbt"
    )
    .enablePlugins(SbtPlugin)
    .dependsOn(common)
