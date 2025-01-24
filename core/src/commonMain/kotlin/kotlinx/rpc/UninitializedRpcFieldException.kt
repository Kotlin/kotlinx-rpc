/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlin.reflect.KProperty

@Deprecated(
    "Use UninitializedRpcFieldException instead",
    ReplaceWith("UninitializedRpcFieldException"),
    level = DeprecationLevel.ERROR,
)
public typealias UninitializedRPCFieldException = UninitializedRpcFieldException

/**
 * Thrown when an uninitialized field of an RPC service is accessed.
 *
 * Use [awaitFieldInitialization] to await for the field initialization
 */
@Deprecated(
    "Fields are deprecated, see https://kotlin.github.io/kotlinx-rpc/0-5-0.html",
    level = DeprecationLevel.WARNING,
)
public class UninitializedRpcFieldException(serviceName: String, property: KProperty<*>) : Exception() {
    override val message: String = "${property.name} field of RPC service \"$serviceName\" in not initialized"
}
