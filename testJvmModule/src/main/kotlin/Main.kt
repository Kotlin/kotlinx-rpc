import org.jetbrains.krpc.RPC
import org.jetbrains.test.TestClass
import org.jetbrains.test.TestList
import org.jetbrains.test.TestList2


fun main() {
    println("Hello, world!")
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
}
