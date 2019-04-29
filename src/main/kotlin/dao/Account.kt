package dao

import org.jetbrains.exposed.dao.EntityID

object Accounts : StringIdTable() {
    val value = double("value")
}

class Account(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<Account>(Accounts)

    var value by Accounts.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        val acc = other as Account
        if (id.value != acc.id.value) return false
        if (value != acc.value) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode() + id.hashCode()
    }


}