/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.client

import kotlinx.rpc.UninitializedRPCFieldException
import kotlin.reflect.KProperty

@Deprecated(
    message = "UninitializedRPCFieldException was moved to kotlinx-rpc-core, to kotlinx.rpc package",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith(
        "UninitializedRPCFieldException(serviceName, property)",
        "kotlinx.rpc.UninitializedRPCFieldException",
    )
)
public class UninitializedRPCFieldException(serviceName: String, property: KProperty<*>): Exception() {
    private val inner = UninitializedRPCFieldException(serviceName, property)

    override val message: String = inner.message
    override val cause: Throwable? = inner.cause
}
