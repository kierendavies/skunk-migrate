package skunk.migrate

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import scala.quoted.*

object Macros:
  val migrationClassRegex = raw"(\d{14})_.*".r
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneOffset.UTC)

  def derivedVersion[M <: Migration[?]: Type](using Quotes): Expr[Version[M]] =
    import quotes.reflect.*

    val classSymbol = TypeRepr.of[M].classSymbol match
      case Some(s) => s
      case None => report.errorAndAbort(s"No class symbol for type ${Type.show[M]}")
    val className = classSymbol.name.stripSuffix("$")

    val timestampString = className match
      case migrationClassRegex(t) => t
      case _ => report.errorAndAbort("Class name must start with timestamp pattern `yyyyMMddHHmmss_`")

    val instant =
      try Instant.from(dateTimeFormatter.parse(timestampString))
      catch
        case e: DateTimeParseException => report.errorAndAbort(s"Invalid yyyyMMddHHmmss timestamp: $timestampString")

    val version = instant.getEpochSecond()
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
