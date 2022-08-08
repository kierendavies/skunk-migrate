package skunk.migrate

trait Version[M <: Migration[?]]:
  def version(migration: M): Long

object Version:
  inline def derived[M <: Migration[?]]: Version[M] =
    ${ VersionMacros.fromClassTimestamp[M] }
