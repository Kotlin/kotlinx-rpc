package org.jetbrains.krpc

import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface RPC

public object KRPC {
    val RPC_SERVICES = mutableMapOf<KType, (RPCEngine) -> RPC>()
}

