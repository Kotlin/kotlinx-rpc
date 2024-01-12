/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal

import kotlinx.serialization.KSerializer

@InternalKRPCApi
class RPCStreamCall(
    val callId: String,
    val streamId: String,
    val stream: Any,
    val kind: StreamKind,
    val elementSerializer: KSerializer<Any?>,
)
