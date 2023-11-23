import $ivy.`de.tototec::de.tobiasroeser.mill.vcs.version::0.3.1`

import de.tobiasroeser.mill.vcs.version.VcsVersion
import mill._
import mill.scalalib._
import mill.scalalib.publish._

object `skunk-migrate` extends ScalaModule with PublishModule {
  def scalaVersion = "3.3.0"

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
    ivy"org.tpolecat::skunk-core:0.6.2"
  )

  def publishVersion = T {
    val vcsState = VcsVersion.vcsState()
    val suffix = if (vcsState.commitsSinceLastTag == 0) "" else "-SNAPSHOT"
    vcsState.format() + suffix
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

  object test extends Tests {
    def testFramework = "munit.Framework"

    def ivyDeps = Agg(
      ivy"org.scalameta::munit::1.0.0-M7",
      ivy"org.typelevel::munit-cats-effect:2.0.0-M3",
    )
  }
}
