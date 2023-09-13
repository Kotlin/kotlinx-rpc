package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer

internal class FlowInfo(
    val callId: String,
    val flowId: String,
    val flow: Flow<*>,
    val elementSerializer: KSerializer<Any>
)