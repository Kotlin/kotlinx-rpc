/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual class ServerMethodDefinition<Request, Response> {
    public actual fun getMethodDescriptor(): MethodDescriptor<Request, Response> {
        TODO("Not yet implemented")
    }

    public actual fun getServerCallHandler(): ServerCallHandler<Request, Response> {
        TODO("Not yet implemented")
    }
}

public actual fun <Request, Response> serverMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    handler: ServerCallHandler<Request, Response>,
): ServerMethodDefinition<Request, Response> {
    TODO("Not yet implemented")
}
