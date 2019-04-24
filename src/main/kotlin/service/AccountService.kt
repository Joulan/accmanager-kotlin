package service

import dao.Account
import dao.Accounts
import dao.DbSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import rest.MoneyTransferDto
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import javax.inject.Inject

interface AccountServiceInt {
    fun runTransfer(moneyTransferDto: MoneyTransferDto)
}

class AccountService
@Inject constructor(
        private val dbSettings: DbSettings,
        private val coroutineScope: CoroutineScope
): AccountServiceInt {

//    init {
////        transaction(dbSettings.db) {
////            addLogger(StdOutSqlLogger)
////            SchemaUtils.drop(Accounts)
////            SchemaUtils.createMissingTablesAndColumns(Accounts)
////            for (i in 0..9) {
////                Account.new("acc00$i") {
////                    value = 1000.0
////                }
////            }
////        }
////    }

    private val locksMap: ConcurrentHashMap<String, ReentrantLock> = ConcurrentHashMap()

    override fun runTransfer(moneyTransferDto: MoneyTransferDto) {
        coroutineScope.launch {
            runTransferSus(moneyTransferDto)
        }
    }

    private fun runTransferSus(moneyTransferDto: MoneyTransferDto) {
        println("runTransferSus")
        val accountFromId = moneyTransferDto.accountFrom
        val accountToId = moneyTransferDto.accountTo
        val moneyValue = moneyTransferDto.moneyValue
        if (moneyValue <= 0.01) {
            return
        }
        val compareResult = accountFromId.compareTo(accountToId)
        val firstLock: Lock
        val secondLock: Lock
        when {
            compareResult == 0 -> return
            compareResult > 0 -> {
                firstLock = getLock(accountFromId)
                secondLock = getLock(accountToId)
            }
            else -> {
                firstLock = getLock(accountToId)
                secondLock = getLock(accountFromId)
            }
        }
        firstLock.lock()
        secondLock.lock()
        sendMoney(accountFromId, accountToId, moneyValue)
        secondLock.unlock()
        firstLock.unlock()
    }

    private fun getLock(accountId: String) = locksMap.getOrPut(accountId) { ReentrantLock() }

    private fun sendMoney(accountFromId: String, accountToId: String, moneyValue: Double) {
        transaction(dbSettings.db) {
            addLogger(StdOutSqlLogger)
            val accountFrom = Account.findById(accountFromId)
            val accountTo = Account.findById(accountToId)
            if (accountFrom == null || accountTo == null) {
                println("account(s) is null")
                return@transaction
            }
            if (accountFrom.value < moneyValue) {
                println("there is not enough money in accountFrom")
                return@transaction
            }
            accountFrom.value -= moneyValue
            accountTo.value += moneyValue
        }
    }

}
