/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.getBinary
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor

/** Echoes the request metadata required by the interop custom metadata case. */
internal object InteropMetadataInterceptor : GrpcServerInterceptor {
    const val ECHO_INITIAL = "x-grpc-test-echo-initial"
    const val ECHO_TRAILING_BIN = "x-grpc-test-echo-trailing-bin"

    override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response> {
        requestHeaders[ECHO_INITIAL]?.let { responseHeaders.append(ECHO_INITIAL, it) }
        requestHeaders.getBinary(ECHO_TRAILING_BIN)?.let { responseTrailers.appendBinary(ECHO_TRAILING_BIN, it) }
        return proceed(request)
    }
}
