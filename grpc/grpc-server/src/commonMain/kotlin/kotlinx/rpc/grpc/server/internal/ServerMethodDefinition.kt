/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public expect class ServerMethodDefinition<Request, Response> {
    public fun getMethodDescriptor(): MethodDescriptor<Request, Response>
    public fun getServerCallHandler(): kotlinx.rpc.grpc.server.internal.ServerCallHandler<Request, Response>
}

@InternalRpcApi
public expect fun <Request, Response> serverMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    handler: kotlinx.rpc.grpc.server.internal.ServerCallHandler<Request, Response>,
): kotlinx.rpc.grpc.server.internal.ServerMethodDefinition<Request, Response>
