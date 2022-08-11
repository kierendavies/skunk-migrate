import mill._
import mill.scalalib._

object `skunk-migrate` extends ScalaModule {
  def scalaVersion = "3.2.0-RC3"

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
      ivy"org.scalameta::munit::1.0.0-M6",
      ivy"org.typelevel::munit-cats-effect:2.0.0-M1",
    )
  }
}
