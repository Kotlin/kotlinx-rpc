package x

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.client.rpcServiceOf

inline fun <reified T : RPC> testGet() = rpcServiceOf<T>(stubEngine)

fun main() {
    val service = rpcServiceOf<MyService>(stubEngine)
}
