import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.*
import org.jetbrains.test.TestClass
import org.jetbrains.test.TestList
import org.jetbrains.test.TestList2
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KType
import kotlin.reflect.typeOf

val stubEngine = object : RPCEngine {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

    override suspend fun call(callInfo: RPCCallInfo): Any? {
        println("Called ${callInfo.methodName}")
        return null
    }
}

fun main() {
    val service = rpcServiceOf<MyService>(stubEngine)

    runBlocking {
        service.empty()
    }

    println(serviceMethodOf<MyService>("empty"))
}

interface MyService : RPC {
    suspend fun empty()
    suspend fun returnType(): String
    suspend fun genericReturnType(): List<String>
    suspend fun doubleGenericReturnType(): List<List<String>>
    suspend fun paramsSingle(arg1: String)
    suspend fun paramsDouble(arg1: String, arg2: String)
    suspend fun varargParams(arg1: String, vararg arg2: String)
    suspend fun genericParams(arg1: List<String>)
    suspend fun doubleGenericParams(arg1: List<List<String>>)
    suspend fun mapParams(arg1: Map<List<String>, Map<Int, Unit>>)
    suspend fun customType(arg1: TestClass): TestClass
    suspend fun nullable(arg1: String?): TestClass?
    suspend fun variance(arg1: List<*>, arg2: TestList<in TestClass>, arg3: TestList2<*>): TestList<out TestClass>?
    suspend fun flow(arg1: Flow<Flow<String>>): Flow<String>

//    companion object : RPCClientProvider<MyService>, RPCMethodClassTypeProvider {
//        override fun client(engine: RPCEngine): MyService {
//            return MyServiceClient(engine)
//        }
//
//        override fun methodClassType(methodName: String): KType? {
//            val companion = MyServiceClient
//            val map = companion.methodNames
//            return map[methodName]
//        }
//    }
}
