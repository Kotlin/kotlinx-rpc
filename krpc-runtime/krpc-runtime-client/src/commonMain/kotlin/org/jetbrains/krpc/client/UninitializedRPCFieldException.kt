package org.jetbrains.krpc.client

import kotlin.reflect.KProperty

/**
 * Thrown when an uninitialized field of an RPC interface is accessed.
 *
 * Use [awaitFieldInitialization] to await for the field initialization
 */
class UninitializedRPCFieldException(serviceName: String, property: KProperty<*>): Exception() {
    override val message: String = "${property.name} field of RPC service \"$serviceName\" in not initialized"
}
