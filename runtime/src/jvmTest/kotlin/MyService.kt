import kotlinx.coroutines.flow.Flow
import org.jetbrains.krpc.RPC

interface MyService : RPC {

    suspend fun unitRequest()

    suspend fun simpleRequest(): String

    suspend fun simpleWithParams(name: String): String

    suspend fun streamRequest(messages: Flow<String>): Int

    suspend fun streamResponse(): Flow<String>
}


