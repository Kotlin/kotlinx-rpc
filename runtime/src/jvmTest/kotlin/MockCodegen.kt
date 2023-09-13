import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.MethodParameters
import org.jetbrains.krpc.RPCCallInfo
import org.jetbrains.krpc.RPCEngine
import java.lang.reflect.Method
import kotlin.reflect.typeOf

@Serializable
class SimpleRequest_Data : MethodParameters {
    override fun asArray(): Array<Any?> = emptyArray()
}

@Serializable
class SimpleWithParams_Data(val name: String) : MethodParameters {
    override fun asArray(): Array<Any?> = arrayOf(name)
}

@Serializable
class StreamRequest_Data(val messages: Flow<String>) : MethodParameters {
    override fun asArray(): Array<Any?> = arrayOf(messages)
}

@Serializable
class StreamResponse_Data : MethodParameters {
    override fun asArray(): Array<Any?> = emptyArray()
}

@Serializable
class UnitRequest_Data : MethodParameters {
    override fun asArray(): Array<Any?> = emptyArray()
}

class MyNotGeneratedClient(private val engine: RPCEngine) : MyService {

    override suspend fun unitRequest() {
        val returnType = typeOf<Unit>()
        engine.call(RPCCallInfo("unitRequest", UnitRequest_Data(), typeOf<UnitRequest_Data>(), returnType))
    }

    override suspend fun simpleRequest(): String {
        val returnType = typeOf<String>()
        val result = engine.call(RPCCallInfo("simpleRequest", SimpleRequest_Data(), typeOf<SimpleRequest_Data>(), returnType))
        check(result is String)
        return result
    }

    override suspend fun simpleWithParams(name: String): String {
        val returnType = typeOf<String>()
        val result = engine.call(RPCCallInfo("simpleWithParams", SimpleWithParams_Data(name), typeOf<SimpleWithParams_Data>(), returnType))
        check(result is String)
        return result
    }

    override suspend fun streamRequest(messages: Flow<String>): Int {
        val result = engine.call(RPCCallInfo("streamRequest", StreamRequest_Data(messages), typeOf<StreamRequest_Data>(), typeOf<Int>()))
        check(result is Int)
        return result
    }

    override suspend fun streamResponse(): Flow<String> {
        val returnType = typeOf<Flow<String>>()
        val result = engine.call(RPCCallInfo("streamResponse", StreamResponse_Data(), typeOf<StreamResponse_Data>(), returnType))
        check(result is Flow<*>)
        return result as Flow<String>
    }
}