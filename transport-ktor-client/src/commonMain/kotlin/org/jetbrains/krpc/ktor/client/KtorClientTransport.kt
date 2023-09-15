package org.jetbrains.krpc.ktor.client

import KtorTransport
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.client.RPCClientEngine
import org.jetbrains.krpc.client.rpcServiceOf

