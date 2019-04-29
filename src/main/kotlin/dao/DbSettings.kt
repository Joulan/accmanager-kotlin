package dao

import org.jetbrains.exposed.sql.Database

interface DbSettings {
    val db: Database
}

object DbSettingsProduction: DbSettings {
    override val db by lazy {
        Database.connect("jdbc:h2:file:~/sample", driver = "org.h2.Driver")
    }
}