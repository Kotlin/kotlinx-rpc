/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package tests

import kotlinx.rpc.krpc.compatibility.TestApiServer
import interfaces.BarInterface
import interfaces.BarInterfaceImpl
import interfaces.BazInterface
import interfaces.BazInterfaceImpl
import interfaces.FooInterface
import interfaces.FooInterfaceImpl
import kotlinx.rpc.RpcServer
import kotlinx.rpc.registerService

@Suppress("unused")
class ApiServer : TestApiServer {
    override fun serveAllInterfaces(rpcServer: RpcServer) {
        rpcServer.apply {
            registerService<FooInterface> { FooInterfaceImpl() }
            registerService<BarInterface> { BarInterfaceImpl() }
            registerService<BazInterface> { BazInterfaceImpl() }
        }
    }
}
