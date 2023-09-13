package org.jetbrains.krpc

import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KType

data class RPCCallInfo(
    val methodName: String,
    val data: Any,
    val dataType: KType,
    val returnType: KType
)

fun RPCCallInfo.returnsFlow() = returnType.classifier == Flow::class
