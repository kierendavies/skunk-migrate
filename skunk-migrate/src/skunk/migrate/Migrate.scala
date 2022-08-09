package skunk.migrate

object Migrate:
  inline def discoverMigrations[F[_]](inline packageName: String): List[Migration[F]] =
    ${ Macros.discoverMigrations[F]('packageName) }
