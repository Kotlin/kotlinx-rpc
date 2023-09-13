package org.jetbrains.krpc

import kotlin.reflect.KType

data class RPCCallInfo(val data: Any, val params: List<KType>, val returnType: KType)
