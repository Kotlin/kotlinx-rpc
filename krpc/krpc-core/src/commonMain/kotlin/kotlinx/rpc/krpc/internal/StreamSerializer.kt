/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor

@Suppress("PropertyName", "detekt.VariableNaming")
@InternalRpcApi
public abstract class StreamSerializer {
    protected val STREAM_ID_SERIAL_NAME: String = "streamId"
    protected val STREAM_ID_SERIALIZER_NAME: String = "StreamIdSerializer"

    protected val streamIdDescriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(STREAM_ID_SERIALIZER_NAME, PrimitiveKind.STRING)
}
