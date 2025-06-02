/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.KSerializer

internal data class StreamCall(
    val callId: String,
    val streamId: String,
    val channel: Channel<Any?>,
    val elementSerializer: KSerializer<Any?>,
)
