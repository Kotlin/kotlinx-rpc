package org.jetbrains.krpc

import kotlin.reflect.KType

data class RPCCallInfo(
    val callableName: String,
    val data: Any,
    val dataType: KType,
    val returnType: KType,
)
