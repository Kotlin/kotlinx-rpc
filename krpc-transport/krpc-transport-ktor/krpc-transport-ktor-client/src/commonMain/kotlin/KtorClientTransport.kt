package org.jetbrains.krpc.transport.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.client.clientOf
import org.jetbrains.krpc.transport.ktor.KtorTransport

@OptIn(InternalCoroutinesApi::class)
suspend inline fun <reified T : RPC> HttpClient.rpc(
    json: Json = Json,
    rpcConfig: RPCConfig.Client = RPCConfig.Client.Default,
    noinline block: HttpRequestBuilder.() -> Unit,
): T {
    val session = webSocketSession(block)
    val transport = KtorTransport(json, session)
    val result = RPC.clientOf<T>(transport, rpcConfig)

    result.coroutineContext.job.invokeOnCompletion(onCancelling = true) {
        transport.cancel()
    }

    return result
}

suspend inline fun <reified T : RPC> HttpClient.rpc(
    urlString: String,
    json: Json = Json,
    rpcConfig: RPCConfig.Client = RPCConfig.Client.Default,
): T = rpc(json, rpcConfig) {
    url(urlString)
}
