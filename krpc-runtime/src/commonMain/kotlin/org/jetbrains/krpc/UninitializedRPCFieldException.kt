package org.jetbrains.krpc

import kotlin.reflect.KProperty

class UninitializedRPCFieldException(serviceName: String, property: KProperty<*>): Exception() {
    override val message: String = "${property.name} field of RPC service \"$serviceName\" in not initialized"
}
