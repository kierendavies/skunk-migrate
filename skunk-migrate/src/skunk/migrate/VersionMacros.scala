package skunk.migrate

import java.time.Instant
import java.time.format.DateTimeParseException
import scala.quoted.*

object VersionMacros:
  def fromClass[M <: Migration[?]: Type](using Quotes): Expr[Version.FromClass[M]] =
    import quotes.reflect.*

    val typeName = TypeRepr.of[M].typeSymbol.name.stripSuffix("$")

    val (derivedTimestamp, derivedName) =
      typeName match
        case s"${timestamp}__${name}" =>
          val instant =
            try Instant.parse(timestamp)
            catch
              case e: DateTimeParseException =>
                report.errorAndAbort(
                  s"Invalid timestamp: $timestamp",
                  Position.ofMacroExpansion,
                )
          (instant.getEpochSecond, name)
        case _ =>
          report.errorAndAbort(
            "Class name must have the format `$timestamp__$name`",
            Position.ofMacroExpansion,
          )

    '{
      new Version.FromClass[M]:
        def timestamp(migration: M): Long = ${ Expr(derivedTimestamp) }
        def name(migration: M): String = ${ Expr(derivedName) }
    }
