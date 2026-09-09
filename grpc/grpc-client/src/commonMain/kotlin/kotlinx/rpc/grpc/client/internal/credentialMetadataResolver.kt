/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlin.coroutines.cancellation.CancellationException

/**
 * Common call credentials metadata resolver.
 *
 * It checks the call credentials requirements, resolves them as metadata,
 * and calls the [applyCallCredentials] function with them.
 * In case of an error, it records the failure with [recordFailure] and returns.
 * In the case of a [CancellationException], it rethrows the exception.
 */
internal suspend inline fun GrpcCallCredentials.applyToMetadata(
    requestAuthority: String,
    requestMethodFullName: String,
    isTransportSecure: Boolean,
    recordFailure: (GrpcStatus) -> Unit,
    applyCallCredentials: (GrpcMetadata) -> Unit
) {
    if (requiresTransportSecurity && !isTransportSecure) {
        recordFailure(GrpcStatus(GrpcStatusCode.UNAUTHENTICATED, "Established channel does not have a sufficient security level to transfer call credential."))
        return
    }

    try {
        val context = GrpcCallCredentials.Context(
            requestAuthority,
            requestMethodFullName
        )
        val metadata = context.getRequestMetadata()
        applyCallCredentials(metadata)
    } catch (err: Throwable) {
        // we are not treating StatusExceptions separately, as currently there is no
        // clean way to support the same feature on native. So for the sake of similar behavior,
        // we always fail with GrpcStatus.UNAVAILABLE. (KRPC-233)
        val description = "Getting metadata from call credentials failed with error: ${err.message}"
        recordFailure(GrpcStatus(GrpcStatusCode.UNAVAILABLE, description, err))
        if (err is CancellationException) throw err
    }
}