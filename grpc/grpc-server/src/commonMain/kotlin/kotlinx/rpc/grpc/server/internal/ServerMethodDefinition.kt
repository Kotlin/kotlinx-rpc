/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public expect class ServerMethodDefinition<Request, Response> {
    public fun getMethodDescriptor(): MethodDescriptor<Request, Response>
    public fun getServerCallHandler(): ServerCallHandler<Request, Response>
}

public expect fun <Request, Response> serverMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    handler: ServerCallHandler<Request, Response>,
): ServerMethodDefinition<Request, Response>
