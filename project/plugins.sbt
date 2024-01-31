resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += Resolver.url("hoya", url("https://maven.pkg.github.com/hiyainc-oss/sbt-plugin"))(Patterns("[organisation]/[module]/[revision]/[artifact].[ext]") )

addSbtPlugin("com.codecommit"                 % "sbt-github-packages"       % "0.5.3")
addSbtPlugin("com.hiya"                       % "sbt-git-versioning"        % "0.2.0")
addSbtPlugin("com.github.sbt"                 % "sbt-native-packager"       % "1.9.16")
addSbtPlugin("org.scalablytyped.converter"    % "sbt-converter"             % "1.0.0-beta44")