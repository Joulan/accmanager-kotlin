import dao.Account
import dao.Accounts
import dao.DbSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineContext
import org.assertj.core.api.Assertions.assertThat
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.test.JerseyTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import rest.MoneyTransferDto
import service.AccountService
import service.AccountServiceInt
import javax.inject.Inject
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import kotlin.coroutines.CoroutineContext
import org.junit.Test as TestV4


class `Application should` : JerseyTest(Application(TestBindings())) {

    @Before
    fun init() {
        transaction(DbSettingsTest.db) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(Accounts)
            SchemaUtils.createMissingTablesAndColumns(Accounts)
            for (i in 0..9) {
                Account.new("acc00$i") {
                    value = 1000.0
                }
            }
        }
    }

    @TestV4
    fun `returns 200 and calculate Robin Hood`() {
        val expected: Map<String, Double> = hashMapOf(
                "acc000" to 100.0,
                "acc001" to 1100.0,
                "acc002" to 1100.0,
                "acc003" to 1100.0,
                "acc004" to 1100.0,
                "acc005" to 1100.0,
                "acc006" to 1100.0,
                "acc007" to 1100.0,
                "acc008" to 1100.0,
                "acc009" to 1100.0
        )
        for (i in 1..9) {
            target("accounts/send").request()
                    .post(
                            Entity.entity(MoneyTransferDto(
                                    "acc000",
                                    "acc00$i",
                                    100.0
                            ), MediaType.APPLICATION_JSON_TYPE)
                    )
        }
        // ToDo: need formalization
        Thread.sleep(2000)
        transaction(DbSettingsTest.db) {
            val all = Account.all()
            for (account in all) {
                val value: Double = expected.getValue(account.id.value)
                assertThat(value).isEqualTo(account.value)
            }
            assertThat(all.toList().size).isEqualTo(expected.size)
        }
    }

    @TestV4
    fun `returns JSON type`() {
        val mediaType = target("accounts/send").request()
                .post(
                        Entity.entity(MoneyTransferDto(
                                "123",
                                "123",
                                0.0
                        ), MediaType.APPLICATION_JSON_TYPE)
                ).mediaType
        assertThat(mediaType).isEqualTo(MediaType.APPLICATION_JSON_TYPE)
    }
}

object DbSettingsTest : DbSettings {
    override val db by lazy {
        Database.connect("jdbc:h2:mem:sample;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
    }
}

// ToDo: save that here
class TestCoroutineScope
@Inject constructor() : CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext = TestCoroutineContext("test")
}

class TestBindings : AbstractBinder() {
    override fun configure() {
        bind(AccountService::class.java).to(AccountServiceInt::class.java)
        bind(DbSettingsTest).to(DbSettings::class.java)
        bind(ProductionGlobalScope::class.java).to(CoroutineScope::class.java)
    }
}
