package skunk.migrate

import java.time.Instant
import java.time.format.DateTimeParseException
import scala.quoted.*

object VersionMacros:
  def fromClassTimestamp[M <: Migration[?]: Type](using Quotes): Expr[Version[M]] =
    import quotes.reflect.*

    val typeName = TypeRepr.of[M].typeSymbol.name.stripSuffix("$")

    val derivedVersion =
      typeName match
        case s"${timestamp}__${_}" =>
          val instant =
            try Instant.parse(timestamp)
            catch
              case e: DateTimeParseException =>
                report.errorAndAbort(
                  s"Invalid timestamp: $timestamp",
                  Position.ofMacroExpansion,
                )
          instant.getEpochSecond
        case _ =>
          report.errorAndAbort(
            "Class name must start with `${timestamp}__`",
            Position.ofMacroExpansion,
          )

    '{ Version[M](${ Expr(derivedVersion) }) }
