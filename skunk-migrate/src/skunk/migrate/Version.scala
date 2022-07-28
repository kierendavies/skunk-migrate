package skunk.migrate

trait Version[M <: Migration[?]]:
  def timestamp(migration: M): Long
  def name(migration: M): String

object Version:
  trait FromClass[M <: Migration[?]] extends Version[M]

  object FromClass:
    inline def derived[M <: Migration[?]]: Version.FromClass[M] =
      ${ VersionMacros.fromClass[M] }
