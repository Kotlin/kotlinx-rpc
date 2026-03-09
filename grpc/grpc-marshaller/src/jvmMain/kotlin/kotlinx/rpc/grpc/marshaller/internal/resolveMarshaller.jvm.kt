/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.rpc.grpc.marshaller.WithGrpcMarshaller
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal actual fun <T : Any> resolveMarshaller(kClass: KClass<T>): Any? {
    val marshallerClass = kClass.findAnnotation<WithGrpcMarshaller>()?.marshaller ?: return null
    return marshallerClass.objectInstance
}
