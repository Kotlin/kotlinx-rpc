/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.codec

import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.reflect.KClass

@ExperimentalRpcApi
@Target(AnnotationTarget.CLASS)
public annotation class WithCodec(val codec: KClass<out MessageCodec<*>>)
