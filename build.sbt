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
      addSbtPlugin("ch.epfl.scala"                  % "sbt-scalafix"              % "0.11.1"),
      addSbtPlugin("com.codecommit"                 % "sbt-github-packages"       % "0.5.3"),
      addSbtPlugin("com.eed3si9n"                   % "sbt-assembly"              % "1.2.0"),
      addSbtPlugin("com.eed3si9n"                   % "sbt-buildinfo"             % "0.11.0"),
      addSbtPlugin("com.geirsson"                   % "sbt-ci-release"            % "1.5.7"),
      addSbtPlugin("com.github.sbt"                 % "sbt-jni"                   % "1.5.4"),
      addSbtPlugin("com.github.sbt"                 % "sbt-native-packager"       % "1.9.16"),
      addSbtPlugin("com.rallyhealth.sbt"            % "sbt-git-versioning"        % "1.6.0"),
      addSbtPlugin("com.timushev.sbt"               % "sbt-updates"               % "0.6.3"),
      addSbtPlugin("io.github.cquiroz"              % "sbt-locales"               % "4.2.0"),
      addSbtPlugin("net.aichler"                    % "sbt-jupiter-interface"     % "0.11.1"),
      addSbtPlugin("nl.gn0s1s"                      % "sbt-dotenv"                % "3.0.0"),
      addSbtPlugin("org.portable-scala"             % "sbt-scalajs-crossproject"  % "1.3.0"),
      addSbtPlugin("org.scala-js"                   % "sbt-scalajs"               % "1.14.0"),
      addSbtPlugin("org.scalablytyped.converter"    % "sbt-converter"             % "1.0.0-beta42"),
      addSbtPlugin("org.scalameta"                  % "sbt-scalafmt"              % "2.4.6"),
      addDependencyTreePlugin
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
