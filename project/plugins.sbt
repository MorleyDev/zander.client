addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "0.7.0-RC2")

resolvers += Classpaths.typesafeReleases

resolvers += "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo"

addSbtPlugin("reaktor" % "sbt-scct" % "0.2-SNAPSHOT")

addSbtPlugin("com.github.theon" %% "xsbt-coveralls-plugin" % "0.0.4")
