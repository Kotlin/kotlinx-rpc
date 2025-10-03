/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.server.internal.ServerCallHandler
import kotlinx.rpc.grpc.server.internal.ServerMethodDefinition
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias ServerMethodDefinition<Request, Response> = io.grpc.ServerMethodDefinition<Request, Response>

@InternalRpcApi
public actual fun <Request, Response> serverMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    handler: ServerCallHandler<Request, Response>,
): ServerMethodDefinition<Request, Response> {
    return io.grpc.ServerMethodDefinition.create(descriptor, handler)
}
