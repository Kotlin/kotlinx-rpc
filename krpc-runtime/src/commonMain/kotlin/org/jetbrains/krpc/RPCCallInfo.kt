/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc

import kotlin.reflect.KType

data class RPCCallInfo(
    val callableName: String,
    val data: Any,
    val dataType: KType,
    val returnType: KType,
)
