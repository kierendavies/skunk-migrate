package skunk.migrate

import scala.quoted.*

object MigrateMacros:
  def discoverMigrations[F[_]: Type](packageName: Expr[String])(using Quotes): Expr[List[Migration[F]]] =
    import quotes.reflect.*

    val packageSymbol = Symbol.requiredPackage(packageName.valueOrAbort)
    val migrationSymbols = packageSymbol.fieldMembers.filter(_.typeRef <:< TypeRepr.of[Migration[F]])
    val migrationExprs = migrationSymbols.map(m => Ident(m.termRef).asExprOf[Migration[F]])

    Expr.ofList(migrationExprs)
