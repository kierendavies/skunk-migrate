package skunk.migrate

import skunk.Session
import skunk.Transaction

trait Migration[F[_]]:
  def up(s: Session[F], xa: Transaction[F]): F[Unit]
  def down(s: Session[F], xa: Transaction[F]): F[Unit]
