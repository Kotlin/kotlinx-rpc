package org.jetbrains.krpc

import kotlinx.serialization.KSerializer

class RPCStreamInfo(
    val callId: String,
    val streamId: String,
    val stream: Any,
    val kind: StreamKind,
    val elementSerializer: KSerializer<Any?>,
)
