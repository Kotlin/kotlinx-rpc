/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlin.reflect.KClass

internal expect fun <T : Any> resolveMarshaller(kClass: KClass<T>): Any?
