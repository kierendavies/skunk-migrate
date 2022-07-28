import mill._
import mill.scalalib._

object `skunk-migrate` extends ScalaModule {
  def scalaVersion = "3.1.3"

  def ivyDeps = Agg(
    ivy"org.tpolecat::skunk-core:0.3.1",
  )
}
