import mill._
import mill.scalalib._

object `skunk-migrate` extends ScalaModule {
  def scalaVersion = "3.1.3"

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
    ivy"org.tpolecat::skunk-core:0.3.1"
  )

  object test extends Tests {
    def testFramework = "munit.Framework"

    def ivyDeps = Agg(
      ivy"org.scalameta::munit::0.7.29"
    )
  }
}
