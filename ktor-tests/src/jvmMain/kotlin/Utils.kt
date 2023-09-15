package sample

import KtorTransport
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.client.RPCClientEngine
import org.jetbrains.krpc.client.rpcServiceOf
import org.jetbrains.krpc.ktor.server.rpc
import org.jetbrains.krpc.server.serviceMethodOf
import kotlin.reflect.typeOf


suspend inline fun <reified T : RPC> HttpClient.rpc(
    json: Json = Json,
    noinline block: HttpRequestBuilder.() -> Unit,
): T {
    val session = webSocketSession(block)
    val transport = KtorTransport(json, session)
    val clientEngine = RPCClientEngine(transport)
    return rpcServiceOf<T>(clientEngine)
}

inline fun <reified T : RPC> Route.rpc(service: T, json: Json = Json) {
    rpc(service, typeOf<T>(), json) { serviceMethodOf<T>(it) }
}

suspend inline fun <reified T : RPC> HttpClient.rpc(urlString: String, json: Json = Json): T = rpc(json) {
    url(urlString)
}
