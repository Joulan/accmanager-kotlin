package dao

import org.jetbrains.exposed.sql.Database
import javax.inject.Inject

interface DbSettings {
    val db: Database
}

class DbSettingsProduction
@Inject constructor(): DbSettings {
    override val db by lazy {
        Database.connect("jdbc:h2:file:~/sample", driver = "org.h2.Driver")
    }
}