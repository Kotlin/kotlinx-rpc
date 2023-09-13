import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.reflect.KType
import kotlin.reflect.typeOf


internal object KRPC {
    val RPC_SERVICES = mutableMapOf<KType, (RPCEngine) -> RPC>()
}

val TMMP = Unit.apply {
    KRPC.RPC_SERVICES[typeOf<MyServiceClient>()] = ::MyServiceClient
}

// User
interface MyService : RPC {

    suspend fun simpleRequest(): String

    suspend fun simpleWithParams(name: String): String

    suspend fun streamRequest(messages: Flow<String>): Int

    suspend fun streamResponse(): Flow<String>
}

val engine: RPCEngine = TODO()

fun main(args: Array<String>) {
    val client: MyService = rpcClientOf<MyService>(engine)

    runBlocking {
        launch {
            client.simpleRequest()
        }.cancel()



        thread {
            runBlocking {
                client.simpleWithParams("name")
            }
        }.interrupt()

        client.streamRequest(flowOf("message"))
        client.streamResponse()
    }
}

inline fun <reified T> rpcClientOf(engine: RPCEngine): T {
    return rpcClientOf(typeOf<T>(), engine)
}

fun <T> rpcClientOf(kType: KType, engine: RPCEngine): T {
    return KRPC.RPC_SERVICES[kType]!!.invoke(engine) as T
}
