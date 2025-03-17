/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.compatibility

import kotlinx.rpc.RpcServer

interface TestApiServer {
    fun serveAllInterfaces(rpcServer: RpcServer)
}
