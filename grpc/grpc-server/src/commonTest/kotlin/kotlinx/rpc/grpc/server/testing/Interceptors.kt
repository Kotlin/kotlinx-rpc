/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor

/** Configures a client call before it forwards the unchanged request flow. */
internal fun clientInterceptor(block: GrpcClientCallScope<*, *>.() -> Unit): GrpcClientInterceptor =
    object : GrpcClientInterceptor {
        override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
            request: Flow<Request>,
        ): Flow<Response> {
            block()
            return proceed(request)
        }
    }

/** Configures a server call before it forwards the unchanged request flow. */
internal fun serverInterceptor(block: GrpcServerCallScope<*, *>.() -> Unit): GrpcServerInterceptor =
    object : GrpcServerInterceptor {
        override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
            request: Flow<Request>,
        ): Flow<Response> {
            block()
            return proceed(request)
        }
    }
