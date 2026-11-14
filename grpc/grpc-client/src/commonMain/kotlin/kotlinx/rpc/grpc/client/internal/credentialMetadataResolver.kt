/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.asException
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlin.coroutines.cancellation.CancellationException

/**
 * Resolves metadata or throws a gRPC status exception for security rejection or provider failure.
 *
 * Security rejection uses UNAUTHENTICATED. Provider failures use UNAVAILABLE.
 */
internal suspend fun GrpcCallCredentials.resolveRequestMetadata(
    requestAuthority: String,
    requestMethodFullName: String,
    isTransportSecure: Boolean,
): GrpcMetadata {
    if (requiresTransportSecurity && !isTransportSecure) {
        throw GrpcStatus(
            GrpcStatusCode.UNAUTHENTICATED,
            "Established channel does not have a sufficient security level to transfer call credential.",
        ).asException()
    }

    return try {
        val context = GrpcCallCredentials.Context(
            requestAuthority,
            requestMethodFullName,
        )
        context.getRequestMetadata()
    } catch (err: Exception) {
        if (err is CancellationException || err is GrpcStatusException) {
            throw err
        }
        // Keep provider status failures consistent across backends: https://youtrack.jetbrains.com/issue/KRPC-233
        throw credentialMetadataFailure(err).asException()
    }
}

internal fun credentialMetadataFailure(error: Throwable): GrpcStatus = GrpcStatus(
    GrpcStatusCode.UNAVAILABLE,
    "Getting metadata from call credentials failed with error: ${error.message}",
    error,
)
