package org.jetbrains.krpc.ktor.client

import KtorTransport
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.client.RPCClientEngine
import org.jetbrains.krpc.client.rpcServiceOf


suspend inline fun <reified T : RPC> HttpClient.RPC(urlString: String, json: Json = Json): T = RPC(json) {
    url(urlString)
}

suspend inline fun <reified T : RPC> HttpClient.RPC(
    json: Json = Json,
    noinline block: HttpRequestBuilder.() -> Unit,
): T {
    val session = webSocketSession(block)
    val transport = KtorTransport(json, session)
    val clientEngine = RPCClientEngine(transport)
    return rpcServiceOf<T>(clientEngine)
}
