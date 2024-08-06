/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <R> KClass<*>.safeCast() = this as R
