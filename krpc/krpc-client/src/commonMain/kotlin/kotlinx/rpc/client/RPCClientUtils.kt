/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.client

import kotlinx.rpc.RPC
import kotlinx.rpc.RPCClient
import kotlinx.rpc.withService
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Deprecated(
    message = "withService was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("withService()", "kotlinx.rpc.withService")
)
public inline fun <reified T : RPC> RPCClient.withService(): T {
    return withService()
}

@Deprecated(
    message = "withService was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("withService(serviceKType)", "kotlinx.rpc.withService")
)
public fun <T : RPC> RPCClient.withService(serviceKType: KType): T {
    return withService(serviceKType)
}

@Deprecated(
    message = "withService was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("withService(serviceKClass)", "kotlinx.rpc.withService")
)
public fun <T : RPC> RPCClient.withService(serviceKClass: KClass<T>): T {
    return withService(serviceKClass)
}
