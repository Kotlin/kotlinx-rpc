package x

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.rpcServiceOf
import org.jetbrains.krpc.server.serviceMethodOf
import sub.SubModuleService
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Serializable
open class TestClass(val value: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if (other !is TestClass) return false
        return value == other.value
    }
}

@Serializable
data class TestList<T : TestClass>(val value: Int = 42)

@Serializable
data class TestList2<out T : TestClass>(val value: Int = 42)

val stubEngine = object : RPCEngine {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

    override suspend fun call(callInfo: RPCCallInfo): Any? {
        println("Called ${callInfo.methodName}")
        return null
    }
}

inline fun <reified T : RPC> get() = rpcServiceOf<T>(stubEngine)
inline fun <reified T : RPC> getName() = serviceMethodOf<T>("empty")

fun main() {
    val service = rpcServiceOf<MyService>(stubEngine)
    val service2 = get<MyService>()
//    val submodule = rpcServiceOf<SubModuleService>(stubEngine)

    val methodName = serviceMethodOf<MyService>("empty")
    println(methodName)
    println(getName<MyService>())

    runBlocking {
//        submodule.hello()
        service.empty()
        service2.empty()
    }

//    val type = serviceMethodOf<MyService>("empty")
//    println(type)
}

interface MyService : RPC {
    suspend fun empty()
//    suspend fun returnType(): String
//    suspend fun genericReturnType(): List<String>
//    suspend fun doubleGenericReturnType(): List<List<String>>
//    suspend fun paramsSingle(arg1: String)
//    suspend fun paramsDouble(arg1: String, arg2: String)
//    suspend fun varargParams(arg1: String, vararg arg2: String)
//    suspend fun genericParams(arg1: List<String>)
//    suspend fun doubleGenericParams(arg1: List<List<String>>)
//    suspend fun mapParams(arg1: Map<List<String>, Map<Int, Unit>>)
//    suspend fun customType(arg1: TestClass): TestClass
//    suspend fun nullable(arg1: String?): TestClass?
//    suspend fun variance(arg2: TestList<in TestClass>, arg3: TestList2<*>): TestList<out TestClass>?
//    suspend fun flow(arg1: Flow<Flow<String>>): Flow<String>
}
