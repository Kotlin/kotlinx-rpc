/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.annotations.Rpc
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

private const val RPC_SERVICE_STUB_SIMPLE_NAME = "\$rpcServiceStub"

internal actual fun <@Rpc T : Any> internalServiceDescriptorOf(kClass: KClass<T>): Any? {
    val className = "${kClass.qualifiedName}\$$RPC_SERVICE_STUB_SIMPLE_NAME"

    return try {
        kClass.java.classLoader
            .loadClass(className)
            ?.kotlin
            ?.companionObjectInstance
    } catch (_ : ClassNotFoundException) {
        null
    }
}
