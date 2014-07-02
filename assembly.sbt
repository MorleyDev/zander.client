import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

packageArchetype.java_application

packageDescription in Debian := "Zander C++ Dependency Management client"

maintainer in Debian := "Jason Morley"

