import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.KRPC
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.rpcClientOf
import kotlin.concurrent.thread
import kotlin.reflect.KType
import kotlin.reflect.typeOf


val INIT_RPC = Unit.apply {
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

