package skunk.migrate

import java.time.Instant
import java.time.format.DateTimeParseException
import scala.quoted.*

object Macros:
  def derivedVersion[M <: Migration[?]: Type](using Quotes): Expr[Version[M]] =
    import quotes.reflect.*

    val typeName = TypeRepr.of[M].typeSymbol.name.stripSuffix("$")

    val timestamp = typeName match
      case s"${timestamp}__${_}" => timestamp
      case _ => report.errorAndAbort("Class name must start with `${timestamp}__`")

    val version =
      try Instant.parse(timestamp).getEpochSecond
      catch case e: DateTimeParseException => report.errorAndAbort(s"Invalid timestamp: $timestamp")

    val versionExpr = Expr(version)

    '{ Version[M]($versionExpr) }

  def discoverMigrations[F[_]: Type](packageNameExpr: Expr[String])(using Quotes): Expr[List[Migration[F]]] =
    import quotes.reflect.*

    val migrationTypeRepr = TypeRepr.of[Migration[F]]
    val migrationTypeName = migrationTypeRepr.show(using Printer.TypeReprShortCode)

    val packageName = packageNameExpr.valueOrAbort
    val packageSymbol = Symbol.requiredPackage(packageName)

    val migrationSymbols =
      packageSymbol.fieldMembers
        .filter(_.typeRef <:< migrationTypeRepr)
        .sortBy(_.name)
    if migrationSymbols.isEmpty then
      report.warning(s"No objects of type $migrationTypeName found in package $packageName")

    val migrationExprs = migrationSymbols.map(m => Ident(m.termRef).asExprOf[Migration[F]])

    Expr.ofList(migrationExprs)
