resolvers += Resolver.sonatypeRepo("snapshots")
Compile / unmanagedSourceDirectories += new File("src/main/scala")

addSbtPlugin("com.codecommit"                 % "sbt-github-packages"       % "0.5.3")
addSbtPlugin("com.github.sbt"                 % "sbt-native-packager"       % "1.10.0")
addSbtPlugin("org.scalablytyped.converter"    % "sbt-converter"             % "1.0.0-beta44")