/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.AssociatedObjectKey
import kotlin.reflect.ExperimentalAssociatedObjects
import kotlin.reflect.KClass

@ExperimentalRpcApi
@AssociatedObjectKey
@OptIn(ExperimentalAssociatedObjects::class)
@Target(allowedTargets = [AnnotationTarget.CLASS])
public actual annotation class WithGrpcMarshaller actual constructor(
    actual val marshaller: KClass<out GrpcMarshaller<*>>
)
