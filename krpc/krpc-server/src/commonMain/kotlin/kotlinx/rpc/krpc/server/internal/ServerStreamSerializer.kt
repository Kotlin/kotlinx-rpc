/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.krpc.internal.StreamSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class ServerStreamSerializer(
    val context: ServerStreamContext,
    val elementType: KSerializer<Any?>,
) : KSerializer<Flow<*>>, StreamSerializer() {
    override val descriptor: SerialDescriptor by lazy {
        buildClassSerialDescriptor("ServerStreamSerializer") {
            element(STREAM_ID_SERIAL_NAME, streamIdDescriptor)
        }
    }

    override fun deserialize(decoder: Decoder): Flow<*> {
        val streamId = decoder.decodeString()

        return context.prepareClientStream(streamId, elementType)
    }

    override fun serialize(encoder: Encoder, value: Flow<*>) {
        error("This method must not be called. Please report to the developer.")
    }
}
