import dao.DbSettings
import dao.DbSettingsProduction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.monitoring.ApplicationEvent
import org.glassfish.jersey.server.monitoring.ApplicationEventListener
import org.glassfish.jersey.server.monitoring.RequestEvent
import org.glassfish.jersey.server.monitoring.RequestEventListener
import service.AccountService
import service.AccountServiceInt
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class Application(bindings: AbstractBinder = ProductionBindings()) : ResourceConfig() {
    init {
        packages("rest")

//        register(LoggingFeature(Logger.getLogger(LoggingFeature.DEFAULT_LOGGER_NAME),
//                Level.INFO,
//                LoggingFeature.Verbosity.PAYLOAD_ANY,
//                Integer.MAX_VALUE)
//        )

//        register(ContextResolver<ObjectMapper> { ObjectMapper().registerModule(KotlinModule()) })

        register(bindings)
        register(ExceptionLogger())
    }
}

class ProductionGlobalScope
@Inject constructor(): CoroutineScope {
    override val coroutineContext: CoroutineContext = GlobalScope.coroutineContext
}

class ProductionBindings : AbstractBinder() {
    override fun configure() {
        bind(AccountService::class.java).to(AccountServiceInt::class.java)
        bind(DbSettingsProduction::class.java).to(DbSettings::class.java)
        bind(ProductionGlobalScope::class.java).to(CoroutineScope::class.java)
    }
}

class ExceptionLogger : ApplicationEventListener, RequestEventListener {
    private val logger = Logger.getAnonymousLogger()

    override fun onEvent(event: ApplicationEvent?) {
    }

    override fun onRequest(requestEvent: RequestEvent): RequestEventListener {
        return this
    }

    override fun onEvent(paramRequestEvent: RequestEvent) {
        if (paramRequestEvent.type != RequestEvent.Type.ON_EXCEPTION) {
            return
        }

        logger.log(Level.SEVERE, "Exception was thrown in request", paramRequestEvent.exception)
    }
}
