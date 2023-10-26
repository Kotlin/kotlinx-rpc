package org.jetbrains.krpc.internal

import kotlinx.serialization.KSerializer

@InternalKRPCApi
class RPCStreamInfo(
    val callId: String,
    val streamId: String,
    val stream: Any,
    val kind: StreamKind,
    val elementSerializer: KSerializer<Any?>,
)
