import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.4.0`

import de.tobiasroeser.mill.vcs.version.VcsVersion
import mill._
import mill.scalalib._
import mill.scalalib.publish._

object `skunk-migrate` extends ScalaModule with PublishModule {
  def scalaVersion = "3.4.3"

  def scalacOptions = Seq(
    "-deprecation",
    "-explain",
    "-feature",
    "-language:implicitConversions",
    "-new-syntax",
    "-source:future",
    "-unchecked",
    "-Xfatal-warnings",
    "-Ykind-projector:underscores",
    "-Ysafe-init",
  )

  def ivyDeps = Agg(
    ivy"org.tpolecat::skunk-core:1.1.0-M3"
  )

  def publishVersion = T {
    VcsVersion.vcsState().format(untaggedSuffix = "-SNAPSHOT")
  }

  def pomSettings = PomSettings(
    description = "Database migrations with Skunk",
    organization = "io.github.kierendavies",
    url = "https://github.com/kierendavies/skunk-migrate",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("kierendavies", "skunk-migrate"),
    developers = Seq(
      Developer("kierendavies", "Kieren Davies", "https://github.com/kierendavies")
    ),
  )

  object test extends ScalaTests {
    def testFramework = "munit.Framework"

    def ivyDeps = Agg(
      ivy"org.scalameta::munit::1.1.1",
      ivy"org.typelevel::munit-cats-effect:2.1.0",
    )
  }
}
