package org.jetbrains.krpc.client

import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCEngine
import kotlin.reflect.KType

expect inline fun <reified T : RPC> rpcServiceOf(engine: RPCEngine): T

expect fun <T : RPC> rpcServiceOf(serviceType: KType, engine: RPCEngine): T
