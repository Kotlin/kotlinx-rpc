import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
class SimpleWithParams_Data(val name: String)

@Serializable
class StreamRequest_Data(val messages: Flow<String>)

class MyServiceClient(private val engine: RPCEngine) : MyService {
    override suspend fun simpleRequest(): String {
        val returnType = typeOf<String>()
        val result = engine.call(CallInfo(Unit, emptyList(), returnType))
        check(result is String)
        return result
    }

    override suspend fun simpleWithParams(name: String): String {
        val returnType = typeOf<String>()
        val result = engine.call(CallInfo(SimpleWithParams_Data(name), listOf(typeOf<String>()), returnType))
        check(result is String)
        return result
    }

    override suspend fun streamRequest(messages: Flow<String>): Int {
        val result = engine.call(CallInfo(StreamRequest_Data(messages), listOf(typeOf<Flow<String>>()), typeOf<Int>()))
        check(result is Int)
        return result
    }

    override suspend fun streamResponse(): Flow<String> {
        val returnType = typeOf<Flow<String>>()
        val result = engine.call(CallInfo(Unit, emptyList(), returnType))
        check(result is Flow<*>)
        return result as Flow<String>
    }
}