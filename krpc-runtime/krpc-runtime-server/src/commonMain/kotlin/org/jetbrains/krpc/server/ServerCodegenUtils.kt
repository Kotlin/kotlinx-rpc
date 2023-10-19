package org.jetbrains.krpc.server

import org.jetbrains.krpc.RPC
import kotlin.reflect.KType

@Suppress("unused")
expect inline fun <reified T : RPC> rpcServiceMethodSerializationTypeOf(methodName: String): KType?

expect fun rpcServiceMethodSerializationTypeOf(serviceType: KType, methodName: String): KType?
