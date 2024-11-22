/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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
public class UninitializedRpcFieldException(serviceName: String, property: KProperty<*>) : Exception() {
    override val message: String = "${property.name} field of RPC service \"$serviceName\" in not initialized"
}
