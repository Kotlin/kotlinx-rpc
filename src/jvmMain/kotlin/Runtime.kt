import sun.management.MethodInfo
import kotlin.reflect.KType

interface RPC

data class CallInfo(val data: Any, val params: List<KType>, val returnType: KType)


class MethodInfo(
    val name: String,
    val kdoc: String,
    val info: String,
    val params: List<KType>,
)

interface RPCEngine {
    suspend fun register(methodInfo: MethodInfo)

    suspend fun call(callInfo: CallInfo): Any
}
