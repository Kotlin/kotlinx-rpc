/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.client.clientOf
import org.jetbrains.krpc.rpcClientConfig
import org.jetbrains.krpc.transport.ktor.KtorTransport
import kotlin.reflect.KClass

private val RPCRequestConfigAttributeKey = AttributeKey<RPCConfigBuilder.Client.() -> Unit>(
    name = "RPCRequestConfigAttributeKey"
)

fun HttpRequestBuilder.rpcConfig(configBuilder: RPCConfigBuilder.Client.() -> Unit = {}) {
    attributes.put(RPCRequestConfigAttributeKey, configBuilder)
}

suspend inline fun <reified T : RPC> HttpClient.rpc(
    urlString: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {},
): T {
    return rpc {
        url(urlString)
        block()
    }
}

suspend inline fun <reified T : RPC> HttpClient.rpc(
    noinline block: HttpRequestBuilder.() -> Unit = {}
): T = rpc(T::class, block)

@OptIn(InternalCoroutinesApi::class)
suspend fun <T : RPC> HttpClient.rpc(
    serviceKClass: KClass<T>,
    block: HttpRequestBuilder.() -> Unit,
): T {
    var requestConfigBuilder: RPCConfigBuilder.Client.() -> Unit = {}
    val session = webSocketSession {
        block()

        attributes.getOrNull(RPCRequestConfigAttributeKey)?.let {
            requestConfigBuilder = it
        }
    }

    val pluginConfigBuilder = attributes.getOrNull(RPCClientPluginAttributesKey)
    val rpcConfig = pluginConfigBuilder?.apply(requestConfigBuilder)?.build()
        ?: rpcClientConfig(requestConfigBuilder)

    val transport = KtorTransport(rpcConfig.serialFormatInitializer.build(), session)
    val result = RPC.clientOf(serviceKClass, transport, rpcConfig)

    result.coroutineContext.job.invokeOnCompletion(onCancelling = true) {
        transport.cancel()
    }

    return result
}
