
lazy val commonSettings = Seq(
    sbtPlugin                                 := true,
    scalaVersion                              := "2.12.18",
    organization                              := "com.harana",
    githubOwner                               := "harana",
    githubRepository                          := "sbt-plugin",
    githubTokenSource                         := TokenSource.Environment("GITHUB_TOKEN"),
    doc / sources                             := Seq(),
    Compile / packageDoc / publishArtifact    := false,
    Compile / packageSrc / publishArtifact    := false,
    resolvers                                 += Resolver.url("hoya", url("https://maven.pkg.github.com/hiyainc-oss/sbt-plugin"))(Patterns("[organisation]/[module]/[revision]/[artifact].[ext]") )
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

      addSbtPlugin("ch.epfl.scala"                  % "sbt-scalafix"              % "0.11.1"),
      addSbtPlugin("com.codecommit"                 % "sbt-github-packages"       % "0.5.3"),
      addSbtPlugin("com.eed3si9n"                   % "sbt-assembly"              % "1.2.0"),
      addSbtPlugin("com.eed3si9n"                   % "sbt-buildinfo"             % "0.11.0"),
      addSbtPlugin("com.github.sbt"                 % "sbt-jni"                   % "1.5.4"),
      addSbtPlugin("com.github.sbt"                 % "sbt-native-packager"       % "1.9.16"),
      addSbtPlugin("com.hiya"                       % "sbt-git-versioning"        % "0.2.0"),
      addSbtPlugin("com.timushev.sbt"               % "sbt-updates"               % "0.6.3"),
      addSbtPlugin("io.github.cquiroz"              % "sbt-locales"               % "4.2.0"),
      addSbtPlugin("net.aichler"                    % "sbt-jupiter-interface"     % "0.11.1"),
      addSbtPlugin("nl.gn0s1s"                      % "sbt-dotenv"                % "3.0.0"),
      addSbtPlugin("org.portable-scala"             % "sbt-scalajs-crossproject"  % "1.3.0"),
      addSbtPlugin("org.scala-js"                   % "sbt-scalajs"               % "1.14.0"),
      addSbtPlugin("org.scalablytyped.converter"    % "sbt-converter"             % "1.0.0-beta44"),
      addSbtPlugin("org.scalameta"                  % "sbt-scalafmt"              % "2.4.6"),
      addDependencyTreePlugin
    )
    .enablePlugins(SbtPlugin)

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
