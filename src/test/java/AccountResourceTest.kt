import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import dao.DbSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineContext
import org.assertj.core.api.Assertions.assertThat
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.test.JerseyTest
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import rest.MoneyTransferDto
import service.AccountService
import service.AccountServiceInt
import javax.inject.Inject
import javax.ws.rs.client.Entity
import javax.ws.rs.core.MediaType
import kotlin.coroutines.CoroutineContext
import org.junit.Test as TestV4


class `HTTP HelloWorldResource should` : JerseyTest(Application(TestBindings())) {
    @TestV4
    fun `returns 200`() {
        val statusCode = target("accounts/send").request()
                .post(
                        Entity.entity(MoneyTransferDto(
                                "123",
                                "123",
                                0.0
                        ), MediaType.APPLICATION_JSON_TYPE)
                ).status
        assertThat(statusCode).isEqualTo(200)
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

class DbSettingsTest
@Inject constructor() : DbSettings {
    override val db by lazy {
        Database.connect("jdbc:h2:mem:sample", driver = "org.h2.Driver")
    }
}

class TestCoroutineScope
@Inject constructor(): CoroutineScope {
    @ObsoleteCoroutinesApi
    override val coroutineContext: CoroutineContext = TestCoroutineContext("test")
}

class TestBindings : AbstractBinder() {
    override fun configure() {
        bind(AccountService::class.java).to(AccountServiceInt::class.java)
        bind(DbSettingsTest::class.java).to(DbSettings::class.java)
        bind(ProductionGlobalScope::class.java).to(CoroutineScope::class.java)
    }
}

@ExtendWith(MockitoExtension::class)
class `HelloWorldResource Should` {

//    private fun createHelloJson(prop1: Int = 1, prop2: String = "test") = HelloJson(prop1, prop2)
//
//    @Mock
//    lateinit var mockDataService: DataService
//
//    @InjectMocks
//    lateinit var helloWorldResource: HelloWorldResource
//
//
//    @Test
//    fun `returns same number of elements as DataService`() {
//        whenever(mockDataService.all()).thenReturn(listOf(createHelloJson(), createHelloJson()))
//
//        assertThat(helloWorldResource.helloWorld()).hasSize(2)
//    }
//
//    @Test
//    fun `contains item from returned list`() {
//        val expectedObject = createHelloJson(prop1 = 2)
//
//        whenever(mockDataService.all()).thenReturn(listOf(expectedObject))
//
//        assertThat(helloWorldResource.helloWorld()).contains(expectedObject)
//    }
}