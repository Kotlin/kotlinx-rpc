package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope

interface RPCEngine : CoroutineScope {
    suspend fun call(callInfo: RPCCallInfo): Any?
}
