import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import org.jetbrains.krpc.RPC

class MyServiceBackend : MyService {
    override suspend fun unitRequest() {}

    override suspend fun simpleRequest(): String {
        return "result"
    }

    override suspend fun simpleWithParams(name: String): String {
        return name.reversed()
    }

    override suspend fun streamRequest(messages: Flow<String>): Int {
        return messages.count()
    }

    override suspend fun streamResponse(): Flow<String> {
        return flow {
            emit("1")
            emit("2")
            emit("3")
        }
    }
}