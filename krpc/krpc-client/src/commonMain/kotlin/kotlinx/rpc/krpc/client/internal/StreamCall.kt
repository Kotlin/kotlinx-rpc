/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

internal data class StreamCall(
    val callId: String,
    val streamId: String,
    val stream: Flow<*>,
    val elementSerializer: KSerializer<Any?>,
    val connectionId: Long?,
    val serviceId: Long?,
)
