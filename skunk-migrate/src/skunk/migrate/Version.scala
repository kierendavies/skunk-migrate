package skunk.migrate

class Version[M <: Migration[?]](val underlying: Long) extends AnyVal

object Version:
  inline def derived[M <: Migration[?]]: Version[M] = ${ VersionMacros.fromClassTimestamp[M] }
