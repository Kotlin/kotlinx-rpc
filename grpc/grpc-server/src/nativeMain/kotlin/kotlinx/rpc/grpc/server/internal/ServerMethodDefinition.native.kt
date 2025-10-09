/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual class ServerMethodDefinition<Request, Response> internal constructor(
    private val methodDescriptor: MethodDescriptor<Request, Response>,
    private val serverCallHandler: ServerCallHandler<Request, Response>,
) {
    public actual fun getMethodDescriptor(): MethodDescriptor<Request, Response> {
        return methodDescriptor
    }

    public actual fun getServerCallHandler(): ServerCallHandler<Request, Response> {
        return serverCallHandler
    }
}

public actual fun <Request, Response> serverMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    handler: ServerCallHandler<Request, Response>,
): ServerMethodDefinition<Request, Response> {
    return ServerMethodDefinition(descriptor, handler)
}
