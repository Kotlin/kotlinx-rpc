/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.annotations.Rpc
import kotlin.reflect.KClass

internal expect fun <@Rpc T : Any> internalServiceDescriptorOf(kClass: KClass<T>): Any?
