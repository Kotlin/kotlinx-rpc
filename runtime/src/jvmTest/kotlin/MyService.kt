import kotlinx.coroutines.flow.Flow
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCClientProvider
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.RPCMethodClassTypeProvider
import kotlin.reflect.KType

interface MyService : RPC {

    suspend fun unitRequest()

    suspend fun simpleRequest(): String

    suspend fun simpleWithParams(name: String): String

    suspend fun streamRequest(messages: Flow<String>): Int

    suspend fun streamResponse(): Flow<String>

    companion object : RPCClientProvider<MyService>, RPCMethodClassTypeProvider {
        override fun client(engine: RPCEngine): MyService {
            return MyNotGeneratedClient(engine)
        }

        override fun methodClassType(methodName: String): KType? {
            TODO("Not yet implemented")
        }
    }
}
