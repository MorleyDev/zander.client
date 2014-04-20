name := "zander.client"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

mainClass in (Compile, run) := Some("uk.co.morleydev.zander.client.Main")

mainClass in oneJar := Some("uk.co.morleydev.zander.client.Main")

libraryDependencies += "com.lambdaworks" % "jacks_2.10" % "2.2.3"

libraryDependencies += "com.stackmob" % "newman_2.10" % "1.3.5"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"
            
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.1.2" % "test"

libraryDependencies += "org.scalacheck" % "scalacheck_2.10" % "1.11.3" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "com.github.kristofa" % "mock-http-server" % "4.0" % "test"