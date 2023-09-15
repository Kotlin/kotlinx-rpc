package sub

import kotlinx.coroutines.runBlocking
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCCallInfo
import org.jetbrains.krpc.RPCEngine
import org.jetbrains.krpc.client.rpcServiceOf
import org.jetbrains.krpc.kRPCMetadata_main__kRPC_codegen_codegen_test_test_submodule
import kotlin.coroutines.CoroutineContext

val stubEngine = object : RPCEngine {
    override val coroutineContext: CoroutineContext
        get() = TODO("Not yet implemented")

    override suspend fun call(callInfo: RPCCallInfo): Any? {
        println("Called ${callInfo.methodName}")
        return null
    }
}

fun main() {
    val service = rpcServiceOf<SubModuleService>(stubEngine)

//    val service = kRPCMetadata_main__kRPC_codegen_codegen_test_test_submodule.rpcServiceOf<SubModuleService>(stubEngine)

    runBlocking {
        service.hello()
    }
}

interface SubModuleService : RPC {
    suspend fun hello()
}
