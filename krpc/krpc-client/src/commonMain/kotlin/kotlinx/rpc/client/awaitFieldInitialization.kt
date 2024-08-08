/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("RedundantSuspendModifier", "UnusedReceiverParameter", "UNUSED_PARAMETER")

package kotlinx.rpc.client

import kotlinx.rpc.RPC
import kotlinx.rpc.awaitFieldInitialization
import kotlin.reflect.KClass

@Deprecated(
    message = "awaitFieldInitialization was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("awaitFieldInitialization(getter)", "kotlinx.rpc.awaitFieldInitialization")
)
public suspend fun <T : RPC, R> T.awaitFieldInitialization(getter: T.() -> R): R {
    return awaitFieldInitialization(getter)
}

@Deprecated(
    message = "awaitFieldInitialization was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("awaitFieldInitialization()", "kotlinx.rpc.awaitFieldInitialization")
)
public suspend inline fun <reified T : RPC> T.awaitFieldInitialization(): T {
    return awaitFieldInitialization()
}

@Deprecated(
    message = "awaitFieldInitialization was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.ERROR,
    replaceWith = ReplaceWith("awaitFieldInitialization(kClass)", "kotlinx.rpc.awaitFieldInitialization")
)
public suspend fun <T : RPC> T.awaitFieldInitialization(kClass: KClass<T>): T {
    return awaitFieldInitialization(kClass)
}
