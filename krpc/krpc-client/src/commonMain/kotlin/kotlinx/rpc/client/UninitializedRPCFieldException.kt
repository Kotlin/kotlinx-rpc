/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.client

import kotlin.reflect.KProperty

/**
 * Thrown when an uninitialized field of an RPC interface is accessed.
 *
 * Use [awaitFieldInitialization] to await for the field initialization
 */
public class UninitializedRPCFieldException(serviceName: String, property: KProperty<*>): Exception() {
    override val message: String = "${property.name} field of RPC service \"$serviceName\" in not initialized"
}
