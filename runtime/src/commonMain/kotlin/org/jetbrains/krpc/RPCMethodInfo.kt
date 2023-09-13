package org.jetbrains.krpc

import kotlin.reflect.KType

class RPCMethodInfo(
    val name: String,
    val kdoc: String,
    val info: String,
    val params: List<KType>,
)
