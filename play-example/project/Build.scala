
import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "jbj-play-example"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "de.leanovate.jbj" % "jbj-core" % "1.0-SNAPSHOT" changing()
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"
  )

}
