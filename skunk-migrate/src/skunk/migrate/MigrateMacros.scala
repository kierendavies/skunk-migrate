package skunk.migrate

import scala.quoted.*

object MigrateMacros:
  def discoverMigrations[F[_]: Type](packageNameExpr: Expr[String])(using Quotes): Expr[List[Migration[F]]] =
    import quotes.reflect.*

    val migrationTypeRepr = TypeRepr.of[Migration[F]]
    val migrationTypeName = migrationTypeRepr.show(using Printer.TypeReprShortCode)

    val packageName = packageNameExpr.valueOrAbort
    val packageSymbol = Symbol.requiredPackage(packageName)

    val migrationSymbols = packageSymbol.fieldMembers.filter(_.typeRef <:< migrationTypeRepr)
    if migrationSymbols.isEmpty then
      report.warning(s"No objects of type $migrationTypeName found in package $packageName")

    val migrationExprs = migrationSymbols.map(m => Ident(m.termRef).asExprOf[Migration[F]])

    Expr.ofList(migrationExprs)
