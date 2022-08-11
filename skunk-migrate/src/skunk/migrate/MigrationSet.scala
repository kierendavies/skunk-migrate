package skunk.migrate

import scala.annotation.tailrec

case class MigrationSet[F[_]](
  appliedIrreversible: List[Migration[F]],
  appliedReversible: List[ReversibleMigration[F]],
  unapplied: List[Migration[F]],
)

object MigrationSet:
  def atVersion[F[_]](migrations: List[Migration[F]], version: Option[Long]): MigrationSet[F] =
    val all = migrations.sortBy(_.version).reverse

    version match
      case None =>
        MigrationSet(
          List.empty,
          List.empty,
          all.reverse,
        )

      case Some(v) =>
        val (unapplied, applied) = all.span(_.version > v)

        val (appliedReversible, appliedIrreversible) = applied.spanMap {
          case rm: ReversibleMigration[F] => Some(rm)
          case _ => None
        }

        MigrationSet(
          appliedIrreversible,
          appliedReversible,
          unapplied.reverse,
        )

  extension [A](l: List[A])
    protected def spanMap[B](f: A => Option[B]): (List[B], List[A]) =
      @tailrec def loop(as: List[A], bs: List[B]): (List[B], List[A]) =
        as match
          case Nil => (bs.reverse, as)
          case a :: asTail =>
            f(a) match
              case Some(b) => loop(asTail, b :: bs)
              case None => (bs.reverse, as)

      loop(l, List.empty)
