import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.*
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.test.BeforeTest
import kotlin.test.Test


inline fun <reified T> rpcClientOf(engine: RPCEngine): T = rpcClientOf(typeOf<T>(), engine)

fun <T> rpcClientOf(kType: KType, engine: RPCEngine): T = when (kType) {
    typeOf<MyService>() -> MyNotGeneratedClient(engine) as T
    else -> error("Unknown type $kType")
}

class CallTest {

    @BeforeTest
    fun init() {
        MY_SERVICE_METHODS[MyService::simpleRequest.name] = typeOf<SimpleRequest_Data>()
        MY_SERVICE_METHODS[MyService::simpleWithParams.name] = typeOf<SimpleWithParams_Data>()
        MY_SERVICE_METHODS[MyService::unitRequest.name] = typeOf<UnitRequest_Data>()
        SERVICES_METHODS[MyService::class] = MY_SERVICE_METHODS
    }

    val transport = StringTransport()
    val clientEngine = RPCClientEngine(transport.client)

    @Test
    fun testCallWithArgs(): Unit = runBlocking {
        val backend = rpcBackendOf<MyService>(MyServiceBackend(), transport.server)
        backend.start()
        val client: MyService = rpcClientOf<MyService>(clientEngine)

        assertEquals("name".reversed(), client.simpleWithParams("name"))
    }

    @Test
    fun testCallWithoutArgs(): Unit = runBlocking {
        val backend = rpcBackendOf<MyService>(MyServiceBackend(), transport.server)
        backend.start()
        val client: MyService = rpcClientOf<MyService>(clientEngine)
        client.unitRequest()
    }
}
