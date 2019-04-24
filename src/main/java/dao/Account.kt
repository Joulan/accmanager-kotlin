package dao

import org.jetbrains.exposed.dao.EntityID

object Accounts : StringIdTable() {
    val value = double("value")
}

class Account(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<Account>(Accounts)

    var value by Accounts.value
}