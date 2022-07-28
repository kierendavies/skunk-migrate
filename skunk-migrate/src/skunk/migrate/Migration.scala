package skunk.migrate

import scala.deriving.Mirror
import scala.quoted.*

import skunk.Session
import skunk.Transaction

sealed trait Migration[F[_]]:
  def up(s: Session[F], xa: Transaction[F]): F[Unit]

trait IrreversibleMigration[F[_]] extends Migration[F]

trait ReversibleMigration[F[_]] extends Migration[F]:
  def down(s: Session[F], xa: Transaction[F]): F[Unit]

object Migration:
  extension [M <: Migration[?]](migration: M)(using version: Version[M])
    def timestamp: Long = version.timestamp(migration)
    def name: String = version.name(migration)
